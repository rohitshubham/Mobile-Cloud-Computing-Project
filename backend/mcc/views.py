import os
import hashlib
import json
from django.core import serializers
from django.conf import settings
from django.views.decorators.csrf import csrf_exempt
from rest_framework import status
from rest_framework.decorators import api_view
from rest_framework.response import Response
from firebase_admin import credentials, auth, initialize_app, db, storage, firestore
from .serializers import UserAuthSerializer, UserSerializer, ProjectSerializer
from .models import UserAuth, User, Project


cred = credentials.Certificate('/home/kibria/MCC/MCCPROJECT/test-mcc-bba43-firebase-adminsdk-1icxf-088bb1f3a5.json')


#cred = credentials.Certificate(os.path.join(settings.BASE_DIR, 'key.json'))

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
    try:
        serializer = ProjectSerializer(data=request.data)
        if serializer.is_valid():
            print(serializer.data)
            #using doc_ref.id as project id
            doc_ref = db.collection(u'projects').document()
            doc_ref.set(serializer.data)            
            return Response({"success" : "created",
                             "project_id":doc_ref.id}, status=status.HTTP_201_CREATED)
        return Response("Invalid project format", status = status.HTTP_206_PARTIAL_CONTENT)
    except Exception as e:
        print(e)
        return Response({"error" : 'InternalException'}, status=status.HTTP_500_INTERNAL_SERVER_ERROR)



def projects_list_helper(project):
    p_obj = project.to_dict()
    p_obj['project_id'] = project.id
    return p_obj

@csrf_exempt
@api_view(['GET'])
def projects_list(request,email_id):
    try:
        # Create a reference to the projects collection
        projects_ref = db.collection(u'projects')

        # Create a query against the collection
        docs = projects_ref.where(u'requester_email', u'==', email_id).stream()

        project_list = [  projects_list_helper(el) for el in docs ]

        for doc in docs:
            print(u'{} => {}'.format(doc.id, doc.to_dict()))        
        return Response({"success": "Done",
                         "project_list":project_list }, status=status.HTTP_200_OK)
    except Exception as e:
        print(e)
        return Response({"error" : 'InternalException'}, status=status.HTTP_500_INTERNAL_SERVER_ERROR)





@csrf_exempt
@api_view(['GET'])
def project_details(request,project_id):
    try:
        # Create a reference to the projects collection
        doc = db.collection(u'projects').document(project_id).get()
 
        project_dict = doc.to_dict()
        project_dict['project_id'] = project_id
        
          
        return Response({"success": "Done",                        
                         "project_info":project_dict }, status=status.HTTP_200_OK)
    except Exception as e:
        print(e)
        return Response({"error" : 'InternalException'}, status=status.HTTP_500_INTERNAL_SERVER_ERROR)