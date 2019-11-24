import os
import hashlib
import json
from datetime import datetime
from django.core import serializers
from django.conf import settings
from django.views.decorators.csrf import csrf_exempt
from rest_framework import status
from rest_framework.decorators import api_view
from rest_framework.response import Response
from firebase_admin import credentials, auth, initialize_app, db, storage, firestore
from .serializers import UserAuthSerializer, UserSerializer, ProjectSerializer, UserProjectSerializer, TaskSerializer, UserTaskSerializer
from .models import UserAuth, User, Project, UserProject

#This is GK's key
#cred = credentials.Certificate('/home/kibria/MCC/MCCPROJECT/test-mcc-bba43-firebase-adminsdk-1icxf-088bb1f3a5.json')


cred = credentials.Certificate(os.path.join(settings.BASE_DIR, 'key.json'))

# ToDO : Update the storage bucket ID
default_app = initialize_app(cred,{
    'storageBucket': 'test-mcc-bba43.appspot.com' #change this
})

bucket = storage.bucket()
db = firestore.client()


@csrf_exempt
@api_view(['POST', 'PUT'])
def user_save(request):
    if request.method == 'POST':         
        for user in auth.list_users().iterate_all():
            user_name = request.data["display_name"]
            if user.display_name == user_name:
                email_hash = str(hashlib.sha256(request.data["email"].encode('utf-8')).hexdigest())
                return Response({"error" : "DisplayNameAlreadyExists",
                                    "suggestion_1"  :  f"{user_name}_{email_hash[0:5]}",
                                    "suggestion_2"  :  f"{user_name}_{email_hash[5:10]}",
                                    "suggestion_3"  :  f"{user_name}_{email_hash[10:15]}"},
                                status=status.HTTP_409_CONFLICT)    

        try:
            user =  auth.create_user(
                            email=request.data["email"],
                            email_verified=False,
                            password=request.data["password"],
                            display_name=request.data["display_name"],
                            photo_url=f'https://profilePhoto/{request.data["email"]}',
                            disabled=False)

            # ToDO : Insert the photo into storage
            
        except auth.EmailAlreadyExistsError:
            return Response({"error" : "EmailAlreadyExists"}, status=status.HTTP_409_CONFLICT)
        except Exception as e:
            print(e)
            return Response({"error" : 'InternalException'}, status=status.HTTP_500_INTERNAL_SERVER_ERROR)
        return Response({"success" : "created"}, status=status.HTTP_201_CREATED)

    if request.method == 'PUT':
        try:
            user = auth.get_user_by_email(request.data["email_id"])
            auth.update_user(uid = user.uid, password = request.data["password"])
            # ToDO : Update the photo

            return Response({"success" : "updated"}, status= status.HTTP_200_OK)
        except Exception as e:
            print(e)
            return Response({"error" : 'InternalException'}, status=status.HTTP_500_INTERNAL_SERVER_ERROR)


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
        return Response({"email": user.email, "display_name" : user.display_name, "photo_url" : user.photo_url}, status=status.HTTP_200_OK)
    except Exception as e:
        print(e)
        return Response({"error" : 'InternalException'}, status=status.HTTP_500_INTERNAL_SERVER_ERROR)

@csrf_exempt
@api_view(['POST', 'PUT'])
def project_save(request):
    if request.method == 'POST':  
        try:
            request.data['creation_time'] = datetime.now()
            serializer = ProjectSerializer(data=request.data)
            if serializer.is_valid():
                #using doc_ref.id as project id
                doc_ref = db.collection(u'projects').document()
                doc_ref.set(serializer.data)
                save_list_project_members(serializer.data["team_members"], doc_ref.id, request.data["requester_email"])

                return Response({"success" : "created",
                                "project_id": doc_ref.id}, status=status.HTTP_201_CREATED)
            return Response("Invalid project format", status = status.HTTP_206_PARTIAL_CONTENT)
        except Exception as e:
            print(e)
            return Response({"error" : 'InternalException'}, status=status.HTTP_500_INTERNAL_SERVER_ERROR)
    if request.method == 'PUT':
        #Saving it before removing 
        project_id = request.data['project_id']
        #Removing it to pass the Serializer
        del request.data['project_id']
        #ToDO: Check creation time logic -it shouldn't change
        request.data['creation_time'] = datetime.now()
        try:
            serializer = ProjectSerializer(data=request.data)
            if serializer.is_valid():
                #using doc_ref.id as project id
                doc_ref = db.collection(u'projects').document(project_id)
                doc_ref.update(serializer.data)

                #Manually adding project id
                request.data['project_id'] = project_id
                remove_team_member(project_id)
                save_list_project_members(serializer.data["team_members"], project_id, request.data["requester_email"])

                return Response({"success" : "Updated",
                                "project_now" : request.data}, status = status.HTTP_200_OK)
                                
            return Response("Invalid project format", status = status.HTTP_206_PARTIAL_CONTENT)
        except Exception as e:
            print(e)
            return Response({"error" : 'InternalException'}, status=status.HTTP_500_INTERNAL_SERVER_ERROR)


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
            value.append({"project_id" : data["project_id"], "is_project_administrator" : data["is_project_administrator"]})
         
        return Response({"success": True,
                         "project_list":value }, status=status.HTTP_200_OK)    
    except Exception as e:
        print(e)
        return Response({"error" : 'InternalException'}, status=status.HTTP_500_INTERNAL_SERVER_ERROR)


@csrf_exempt
@api_view(['GET','DELETE'])
def project_details(request,project_id):
    if request.method == 'GET':  
        try:
            # Create a reference to the projects collection
            doc = db.collection(u'projects').document(project_id).get()

            if not doc.exists:
                 return Response({"error": "Not Found"}, status=status.HTTP_404_NOT_FOUND)
    
            project_dict = doc.to_dict()
            project_dict['project_id'] = project_id
            
            
            return Response({"success": "Found",                        
                            "project_info":project_dict }, status=status.HTTP_200_OK)
        except Exception as e:
            print(e)
            return Response({"error" : 'InternalException'}, status=status.HTTP_500_INTERNAL_SERVER_ERROR)

    if request.method == 'DELETE':  
        try:

             # Create a reference to the projects collection
            doc = db.collection(u'projects').document(project_id)

            if not doc.get().exists:
                 return Response({"error": "Not Found"}, status=status.HTTP_404_NOT_FOUND)

            
            doc.delete()            
            return Response({"success": "Deleted" }, status=status.HTTP_200_OK)
        except Exception as e:
            print(e)
            return Response({"error" : 'InternalException'}, status=status.HTTP_500_INTERNAL_SERVER_ERROR)
       
def remove_team_member(project_id):
    try:
        projects_ref = db.collection(u'userProjects')
        docs = projects_ref.where(u'project_id',u'==', project_id).get()
        for doc in docs:
            projects_ref.document(doc.id).delete()    
    except Exception as e:
        print(e)
    

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
            task_id.document(doc.id).delete()    
    except Exception as e:
        print(e)


@csrf_exempt
@api_view(["POST"])
def task_save(request):
    try:
        serializer = TaskSerializer(data=request.data)
        if serializer.is_valid():       
            doc_ref = db.collection(u'tasks').document()
            doc_ref.set(serializer.data)       
            return Response({"success" : "created",
                            "task_id": doc_ref.id}, status = status.HTTP_201_CREATED)
    except Exception as e:
        print(e)
        return Response({"error" : 'InternalException'}, status=status.HTTP_500_INTERNAL_SERVER_ERROR)
    

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
        
        for doc in docs:
            data = doc.to_dict()
            if data["is_project_administrator"] == "false":
                return Response({"error": "not_allowed"}, status = status.HTTP_403_FORBIDDEN)


        remove_task_members(task_id)
        member_list = members.split(",")

        for member in member_list:
            data = {'email_id' : member, 'task_id': task_id, 'project_id' :  project_id  }
            serializer_user_task = UserTaskSerializer(data = data)

            db.collection(u'userTasks').document().set(serializer_user_task.initial_data)

        return Response({"success" : "saved"}, status = status.HTTP_200_OK)

    except Exception as e:
        print(e)
        return Response({"error" : 'InternalException'}, status=status.HTTP_500_INTERNAL_SERVER_ERROR)



    
