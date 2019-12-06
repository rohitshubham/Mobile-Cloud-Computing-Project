import os
import hashlib
import json
import re
from datetime import datetime
import dateutil.parser
from django.core import serializers
from django.conf import settings
from django.views.decorators.csrf import csrf_exempt
from rest_framework import status
from rest_framework.decorators import api_view
from rest_framework.response import Response
from firebase_admin import credentials, auth, initialize_app, db, storage, firestore
from .serializers import UserAuthSerializer, UserSerializer, ProjectSerializer, UserProjectSerializer, TaskSerializer, UserTaskSerializer
from .models import UserAuth, User, Project, UserProject
from .render import Render
from django.utils import timezone
from django.utils.dateparse import parse_datetime




cred = credentials.Certificate(os.path.join(settings.BASE_DIR, 'key.json'))

# ToDO : Update the storage bucket ID
default_app = initialize_app(cred,{
    'storageBucket': 'mcc-fall-2019-g14.appspot.com' 
})

bucket = storage.bucket()
db = firestore.client()

#========================================================================

def add_project_event(project_id, message):
    event = { "project_id" : project_id, "event" : message }
    db.collection(u'projectEvents').add(event)


def upload_blob(file_obj,filename):   
    blob = bucket.blob(filename)
    blob.upload_from_file(file_obj)
    
    
   
    

@csrf_exempt
@api_view(['POST', 'PUT'])
def user_save(request):
    if request.method == 'POST':         
        for user in auth.list_users().iterate_all():
            user_name = request.data["display_name"]
            if user.display_name == user_name:
                email_hash = str(hashlib.sha256(request.data["email_id"].encode('utf-8')).hexdigest())
                return Response({ "success" : "false", "error" : "DisplayNameAlreadyExists",
                                    "payload": {
                                        "suggestion_1"  :  f"{user_name}_{email_hash[0:5]}",
                                        "suggestion_2"  :  f"{user_name}_{email_hash[5:10]}",
                                        "suggestion_3"  :  f"{user_name}_{email_hash[10:15]}"}
                                        },
                                status=status.HTTP_409_CONFLICT)    

        try:
            profile_pic = 'https://empty.com'
                
            if request.data["display_name"] is not None:
                user =  auth.create_user(
                            email=request.data["email_id"],
                            email_verified=False,
                            password=request.data["password"],
                            display_name=request.data["display_name"],
                            photo_url=profile_pic,
                            disabled=False)
            else:
                return Response({"error" : "DisplayNameNotProvided", "success" : "false"}, status=status.HTTP_400_BAD_REQUEST)           
            
        except auth.EmailAlreadyExistsError:
            return Response({"error" : "EmailAlreadyExists", "success" : "false"}, status=status.HTTP_409_CONFLICT)
        except Exception as e:
            print(e)
            return Response({"error" : 'InternalException', "success" : "false"}, status=status.HTTP_500_INTERNAL_SERVER_ERROR)
        return Response({"success" : "true"}, status=status.HTTP_201_CREATED)

    if request.method == 'PUT':
        try:
            user = auth.get_user_by_email(request.data["email_id"])
            
            file_obj = request.FILES.get('file', False)

            if file_obj is not False:
                ext = file_obj.name.split('.')[-1]
                profile_pic = ''
                profile_pic = 'https://storage.cloud.google.com/mcc-fall-2019-g14.appspot.com/' + request.data["email_id"] + '.'+ext+'?authuser=1'
                auth.update_user(uid = user.uid, profile_pic = profile_pic)
                upload_blob(file_obj.file, request.data["email_id"]+'.'+ext)

            
            if 'password' in request.data:
                auth.update_user(uid = user.uid, password = request.data["password"])



            return Response({"success" : "true"}, status= status.HTTP_200_OK)
        except Exception as e:
            print(e)
            return Response({"error" : 'InternalException', "success" : "false"}, status=status.HTTP_500_INTERNAL_SERVER_ERROR)


def save_list_project_members(members, project_id, requester_email):
    list_members = members.split(",")
    for member in list_members:
        data = {'email_id' : member, 'project_id': project_id, 'is_project_administrator' :  member == requester_email  }
        serializer_user_project = UserProjectSerializer(data = data)

        db.collection(u'userProjects').document().set(serializer_user_project.initial_data)

           
@csrf_exempt
@api_view(['GET'])
def user_get(request, email_id):
    try:
        user = auth.get_user_by_email(email_id)
        data = {"email": user.email, "display_name" : user.display_name, "photo_url" : user.photo_url}        
        return Response({"success": "true", "payload" : data}, status=status.HTTP_200_OK)
    except Exception as e:
        print(e)
        return Response({"error" : 'InternalException', "success": "false"}, status=status.HTTP_500_INTERNAL_SERVER_ERROR)

@csrf_exempt
@api_view(['POST', 'PUT'])
def project_save(request):
    if request.method == 'POST':  
        try:
            request.data['creation_time'] = datetime.now()
            request.data['last_modified'] = datetime.now()
            #Max wanted 2019-12-03'T'03:09, so accepting by doing following change
            if 'deadline' in request.data:
                try:
                    request.data['deadline'] = dateutil.parser.parse(request.data['deadline'])
                except:
                    dl = request.data['deadline'].replace("'","")+":00"
                    request.data['deadline'] = parse_datetime(dl)
            
            #check if the project with same exists for this user
            projects_present = db.collection('projects').where("name", "==", request.data["name"]).where("requester_email", "==", request.data["requester_email"]).stream()

            for project_present in projects_present:
                return Response({"success" : "false", "error" : "ProjectAlreadyExists"}, status= status.HTTP_409_CONFLICT)

            file_obj = request.FILES.get('file', False)
            badge = 'https://empty.com'
            ext = ''
            if file_obj is not False:
                ext = file_obj.name.split('.')[-1]
                badge = ''
                badge = f'https://storage.cloud.google.com/mcc-fall-2019-g14.appspot.com/{request.data["requester_email"]}_{request.data["name"]}.{ext}?authuser=1'

            request.data["badge"] = badge

            serializer = ProjectSerializer(data=request.data)
            
            if serializer.is_valid():
                
                #using doc_ref.id as project id
                doc_ref = db.collection(u'projects').document()
                doc_ref.set(serializer.data)
                save_list_project_members(serializer.data["team_members"], doc_ref.id, request.data["requester_email"])
                add_project_event(doc_ref.id, f"{datetime.now().strftime('%Y-%m-%d %H:%M')} - Project was created by {auth.get_user_by_email(request.data['requester_email']).display_name}.")
                
                
                if file_obj is not False:
                    ext = file_obj.name.split('.')[-1]
                    upload_blob(file_obj.file, f'{request.data["requester_email"]}_{request.data["name"]}.{ext}')

                return Response({"success" : "true",
                                "payload" : {"project_id": doc_ref.id}}, status=status.HTTP_201_CREATED)

            return Response({"error" : "Invalid project format", "success": "false"}, status = status.HTTP_206_PARTIAL_CONTENT)
        except Exception as e:
            print(e)
            return Response({"error" : 'InternalException', "success": "false"}, status=status.HTTP_500_INTERNAL_SERVER_ERROR)
    if request.method == 'PUT':
        #Saving it before removing 
        project_id = request.data['project_id']
        #Removing it to pass the Serializer
        del request.data['project_id']
        request.data['last_modified'] = datetime.now()
        try:
            if 'deadline' in request.data:
                try:
                    request.data['deadline'] = dateutil.parser.parse(request.data['deadline'])
                except:
                    dl = request.data['deadline'].replace("'","")+":00"
                    request.data['deadline'] = parse_datetime(dl)

            file_obj = request.FILES.get('file', False)

            doc_ref = db.collection(u'projects').document(project_id)
            document = doc_ref.get().to_dict()
            request.data["badge"] = document["badge"]

            serializer = ProjectSerializer(data=request.data)
            if serializer.is_valid():
                
                doc_ref.update(serializer.data)

                #Manually adding project id
                request.data['project_id'] = project_id
                remove_team_member(project_id)
                save_list_project_members(serializer.data["team_members"], project_id, request.data["requester_email"])

                add_project_event(doc_ref.id, f"{datetime.now().strftime('%Y-%m-%d %H:%M')} - Project was edited by {auth.get_user_by_email(request.data['requester_email']).display_name}.")
                
                if file_obj is not False:
                    ext = file_obj.name.split('.')[-1]
                    upload_blob(file_obj.file, f'{request.data["requester_email"]}_{request.data["name"]}.{ext}')

                del request.data["file"]
                return Response({"success" : "true",
                                "payload" : request.data }, status = status.HTTP_200_OK)
                                
            return Response({"error" : "Invalid project format", "success": "false"}, status = status.HTTP_206_PARTIAL_CONTENT)
        except Exception as e:
            print(e)
            return Response({"error" : 'InternalException', "success": "false"}, status=status.HTTP_500_INTERNAL_SERVER_ERROR)


#Just to add Project_id manually to each project object
def projects_list_helper(project):
    p_obj = project.to_dict()
    p_obj['project_id'] = project.id
    return p_obj

@csrf_exempt
@api_view(['GET'])
def projects_list(request,email_id):
    try: 
        # Create a reference to the projects collection
        projects_ref = db.collection(u'userProjects')

        # Create a query against the collection
        docs = projects_ref.where(u'email_id', u'==', email_id).stream()        
        
        value = []

        for x in docs:
            data = x.to_dict()
            project_id = data["project_id"]
            project = db.collection(u'projects').document(project_id).get().to_dict()
            if project is not None:  
                project["project_id"] = project_id
                project["is_project_administrator"]  =  data["is_project_administrator"]           
                value.append(project)
                
                
         
        return Response({"success": "true",
                         "payload": value }, status=status.HTTP_200_OK)    
    except Exception as e:
        print(e)
        return Response({"error" : 'InternalException' , "success": "false"}, status=status.HTTP_500_INTERNAL_SERVER_ERROR)


@csrf_exempt
@api_view(['GET','DELETE'])
def project_details(request,project_id):
    if request.method == 'GET':  
        try:
            # Create a reference to the projects collection
            doc = db.collection(u'projects').document(project_id).get()

            if not doc.exists:
                 return Response({"error": "ProjectNotFound", "success": "false"}, status=status.HTTP_404_NOT_FOUND)
    
            project_dict = doc.to_dict()
            project_dict['project_id'] = project_id
            
            
            return Response({"success": "true",                        
                            "payload" : project_dict }, status=status.HTTP_200_OK)
        except Exception as e:
            print(e)
            return Response({"error" : 'InternalException' , "success": "false"}, status=status.HTTP_500_INTERNAL_SERVER_ERROR)

    if request.method == 'DELETE':  
        try:

             # Create a reference to the projects collection
            doc = db.collection(u'projects').document(project_id)

            if not doc.get().exists:
                 return Response({"error": "ProjectNotFound" , "success": "false"}, status=status.HTTP_404_NOT_FOUND)
            
            remove_team_member(project_id)            
            doc.delete()            
            return Response({"success": "true"}, status=status.HTTP_200_OK)
        except Exception as e:
            print(e)
            return Response({"error" : 'InternalException', "success": "false"}, status=status.HTTP_500_INTERNAL_SERVER_ERROR)
       
def remove_team_member(project_id):
    try:
        projects_ref = db.collection(u'userProjects')
        docs = projects_ref.where(u'project_id',u'==', project_id).get()
        for doc in docs:
            projects_ref.document(doc.id).delete()    
    except Exception as e:
        print(e)


#search a project using keyword
@csrf_exempt
@api_view(["POST","GET"])
def search_project_by_keyword(request):
    #For getting a list of keywords
    if request.method == 'GET':
        try:
            docs = db.collection(u'projects').stream()
            keywords = []
            for doc in docs:
                doc_dict = doc.to_dict()
                proj_kwords = doc_dict['keywords'].split(',')
                proj_kwords = set(proj_kwords)-set(keywords)
                keywords = keywords + list(proj_kwords)
                #print(u'{} => {}'.format(doc.id, doc.to_dict()))
            sorted_keywords = sorted(keywords, key=str.casefold)            
            return Response({"success": "true","payload":sorted_keywords}, status=status.HTTP_200_OK)
        except Exception as e:
            print(e)
            return Response({"error" : 'InternalException', "success": "false"}, status=status.HTTP_500_INTERNAL_SERVER_ERROR)
    #Given a keyword returns the projects that matched with the keyword
    if request.method == 'POST':
        try:
            search_keyword = request.data['search_keyword']
            docs = db.collection(u'projects').stream()
            matched_projects = []
            for doc in docs:
                doc_dict = doc.to_dict()
                proj_kwords = doc_dict['keywords'].split(',')
                if search_keyword in proj_kwords:
                    matched_projects.append(doc_dict)
            return Response({"success": "true","payload":matched_projects}, status=status.HTTP_200_OK)  

        except Exception as e:
            print(e)
            return Response({"error" : 'InternalException', "success": "false"}, status=status.HTTP_500_INTERNAL_SERVER_ERROR)

#search a project using name
@csrf_exempt
@api_view(["POST"])
def search_project_by_name(request):
    #Given a name or partial name, returns the projects that matched with the input
    if request.method == 'POST':
        try:
            search_keyword = request.data['search_name']
            docs = db.collection(u'projects').stream()
            matched_projects = []
            for doc in docs:
                doc_dict = doc.to_dict()
                if re.search(search_keyword, doc_dict['name'], re.IGNORECASE):
                    matched_projects.append(doc_dict)
            return Response({"success": "true","payload":matched_projects}, status=status.HTTP_200_OK)  

        except Exception as e:
            print(e)
            return Response({"error" : 'InternalException', "success": "false"}, status=status.HTTP_500_INTERNAL_SERVER_ERROR)

#=================================================================================

def save_list_task_members(members, project_id, requester_email):
    list_members = members.split(",")
    for member in list_members:
        data = {'email_id' : member, 'project_id': project_id, 'is_project_administrator' :  member == requester_email  }
        serializer_user_project = UserProjectSerializer(data = data)

        db.collection(u'userProjects').document().set(serializer_user_project.initial_data)

def remove_task_members(task_id):
    try:
        tasks_ref = db.collection(u'userTasks')
        docs = tasks_ref.where(u'task_id',u'==', task_id).get()
        for doc in docs:
            tasks_ref.document(doc.id).delete()    
    except Exception as e:
        print(e)

#If member count is sent as -1, we consider it as completed
def change_task_status(task_id, project_id, member_count = -1):

    task_name = db.collection('tasks').document(task_id).get().to_dict()["name"]
    message = ""

    if member_count == 0:
        db.collection(u"tasks").document(task_id).update({"status" : "P"})
        message =  "All Members removed. Status changed back to Pending"
    elif member_count == -1:
        db.collection(u"tasks").document(task_id).update({"status" : "C"})
        message = "Status changed to Completed"
    else:
        db.collection(u"tasks").document(task_id).update({"status" : "O"})
        message = "Members edited. Status changed to Pending"

    add_project_event(project_id, f"{datetime.now().strftime('%Y-%m-%d %H:%M')} - {task_name} - {message}")



#ToDO: If we have time, add endpoint for editing the task
@csrf_exempt
@api_view(["POST"])
def task_save(request):
    try:
        request.data['creation_time'] = datetime.now()
        request.data['last_modified'] = datetime.now()
        serializer = TaskSerializer(data=request.data)
        if serializer.is_valid():       
            doc_ref = db.collection(u'tasks').document()
            doc_ref.set(serializer.data)

            requester_email  = db.collection(u'projects').document(serializer.data["project_id"]).get().to_dict()["requester_email"]

            task_name = db.collection('tasks').document(doc_ref.id).get().to_dict()["name"]

            add_project_event(doc_ref.id, f"{datetime.now().strftime('%Y-%m-%d %H:%M')} - Task {task_name} was created by {auth.get_user_by_email(requester_email).display_name}.")       
            return Response({"success" : "true",
                            "payload": doc_ref.id}, status = status.HTTP_201_CREATED)
    except Exception as e:
        print(e)
        return Response({"error" : 'InternalException', "success": "false"}, status=status.HTTP_500_INTERNAL_SERVER_ERROR)
    



@csrf_exempt
@api_view(['GET'])
def user_typeahead(request, search_path):
    if len(search_path) < 3:
        return Response({"success" : "false", "error" : "InsufficientQueryLength"}, status= status.HTTP_400_BAD_REQUEST)
    try:
        filtered_user = []
        for user in auth.list_users().iterate_all():
            if len(filtered_user) == 5:
                break
            if user.display_name is None:
                continue
            if search_path.lower() in user.display_name.lower():
                filtered_user.append(user)
        
        users = [{"display_name" : x.display_name, "email_id" : x.email} for x in filtered_user]
        return Response({"success" : "true", "payload" : users}, status = status.HTTP_200_OK)
    except Exception as e:    
        print(e)
        return Response({"error" : 'InternalException', "success": "false"}, status=status.HTTP_500_INTERNAL_SERVER_ERROR)


#=======================PDF generation=============================


@csrf_exempt
@api_view(["GET"])
def generate_report(request,project_id):
    try:
        actual_project = db.collection(u'projects').document(project_id).get()

        project_dict = actual_project.to_dict()
      

        #Getting all the members
        projects_ref = db.collection(u'userProjects')
        docs = projects_ref.where(u'project_id',u'==', project_id).stream()
        value = []
        for x in docs:
            data = x.to_dict()
            #Getting Displayname via Firebase-admin
            user = auth.get_user_by_email(data["email_id"])
            value.append({"displayName" : user.display_name})
        today = timezone.now()

        #Getting the events
        events_ref = db.collection(u'projectEvents')
        events = events_ref.where(u'project_id',u'==', project_id).stream()


        events_list = []
        for x in events:
            data = x.to_dict()         
            events_list.append(data)

        print(events_list)
    

        params = {
            'today': today,
            'members': value,
            'project': project_dict ,
            'events': events_list 
        }
        return Render.render('pdf.html', params)
         
    except Exception as e:    
        print(e)
        return Response({"error" : 'InternalException', "success": "false"}, status=status.HTTP_500_INTERNAL_SERVER_ERROR)
        
@csrf_exempt
@api_view(["POST"])
def add_member_to_task(request):
    try:
        task_id = request.data["task_id"]
        members = request.data["members"]
        project_id = request.data["project_id"]
        requester_email = request.data["requester_email"]

        projects_ref = db.collection(u'userProjects')
        docs = projects_ref.where(u'project_id',u'==', project_id).where(u'email_id', u'==', requester_email).get()
        
        #If the number of docs is zero then return forbidden
        if sum(1 for _ in docs) == 0:
            return Response({"error": "not_allowed", "success": "false"}, status = status.HTTP_403_FORBIDDEN)

        #if there is doc but he is not the admin, return forbidden
        for doc in docs:
            data = doc.to_dict()
            if data["is_project_administrator"] == "false":
                return Response({"error": "not_allowed", "success": "false"}, status = status.HTTP_403_FORBIDDEN)


        remove_task_members(task_id)
        
        if members:
            member_list = members.split(",")

            for member in member_list:
                data = {'email_id' : member, 'task_id': task_id, 'project_id' :  project_id  }
                serializer_user_task = UserTaskSerializer(data = data)
                if serializer_user_task.is_valid():    
                    db.collection(u'userTasks').document().set(serializer_user_task.initial_data)
                else:
                    return Response({"error": "unable_to save", "success": "false"}, status = status.HTTP_422_UNPROCESSABLE_ENTITY)

        # Change the status
        tasks_ref = db.collection(u"userTasks")
        task_members = tasks_ref.where(u'task_id',u"==", task_id).get()

        change_task_status(task_id, project_id, sum(1 for _ in task_members)) 

        return Response({"success" : "true"}, status = status.HTTP_200_OK)

    except Exception as e:
        print(e)
        return Response({"error" : 'InternalException', "success": "false"}, status=status.HTTP_500_INTERNAL_SERVER_ERROR)

@csrf_exempt
@api_view(["POST"])
def set_task_completed(request):
    try:
        task_id = request.data["task_id"]
        project_id = request.data["project_id"]
        requester_email = request.data["requester_email"]

        add_project_event(project_id, f"{datetime.now().strftime('%Y-%m-%d %H:%M')} - Status change to complete requested by {requester_email}.")


        change_task_status(task_id, project_id)

        return Response({"success": "true"}, status = status.HTTP_200_OK)

    except Exception as e:
        print(e)
        return Response({"error" : 'InternalException', "success": "false"}, status=status.HTTP_500_INTERNAL_SERVER_ERROR)


    

@csrf_exempt
@api_view(["GET"])
def task_retrive(request,project_id):
    try: 
        # Create a reference to the projects collection
        projects_ref = db.collection(u'userTasks')

        # Create a query against the collection
        docs = projects_ref.where(u'project_id', u'==', project_id).stream()        
        
        value = []

        for x in docs:
            data = x.to_dict()
            task_id = data["task_id"]
            task = db.collection(u'tasks').document(task_id).get().to_dict()
            if task is not None:  
                task["task_id"] = task_id
                task["email_id"]  =  data["email_id"]           
                value.append(task)
                
                
         
        return Response({"success": "true",
                         "payload": value }, status=status.HTTP_200_OK)    
    except Exception as e:
        print(e)
        return Response({"error" : 'InternalException' , "success": "false"}, status=status.HTTP_500_INTERNAL_SERVER_ERROR)