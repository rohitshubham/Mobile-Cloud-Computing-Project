from rest_framework import status
from rest_framework.decorators import api_view
from rest_framework.response import Response
from .serializers import UserAuthSerializer,UserSerializer
from .models import UserAuth,User
from django.views.decorators.csrf import csrf_exempt
from firebase_admin import credentials, auth, initialize_app, db, storage
import os
from django.conf import settings
import hashlib

cred = credentials.Certificate(os.path.join(settings.BASE_DIR, 'key.json'))

# ToDO : Update the storage bucket ID
default_app = initialize_app(cred,{
    'storageBucket': 'test-mcc-bba43.appspot.com'
})

bucket = storage.bucket()


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

    if request.method== 'PUT':
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

