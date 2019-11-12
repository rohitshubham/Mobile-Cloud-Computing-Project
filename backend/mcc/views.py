from rest_framework import status
from rest_framework.decorators import api_view
from rest_framework.response import Response
from .serializers import UserAuthSerializer,UserSerializer
from .models import UserAuth,User
from django.views.decorators.csrf import csrf_exempt

import pyrebase 


config = {
    'apiKey': "AIzaSyAZwRL3Oz-rJ3vx9U_IBrzqUDF2EcAj4kk",
    'authDomain': "test-mcc-bba43.firebaseapp.com",
    'databaseURL': "https://test-mcc-bba43.firebaseio.com",
    'projectId': "test-mcc-bba43",
    'storageBucket': "test-mcc-bba43.appspot.com",
    'messagingSenderId': "162360953834",
    'appId': "1:162360953834:web:b345e4c8a6f5d3489cc093"

}


def index(request):
    return HttpResponse("Hello, world. You're at the Mobile Cloud Computing index.")




@csrf_exempt
@api_view(['POST'])
def user_list(request):
    """
    List all code snippets, or create a new snippet.
    """

    firebase = pyrebase.initialize_app(config)
    auth = firebase.auth()

  
    if request.method == 'POST':
        serializer = UserAuthSerializer(data=request.data)
        if serializer.is_valid():
            #serializer.save()
            print(serializer.data['email'])
            try:
                user = auth.create_user_with_email_and_password(serializer.data['email'],serializer.data['password'])
                   
               
            except Exception as e:
                print(e)
                return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)
            return Response(serializer.data, status=status.HTTP_201_CREATED)
               
           
        return Response(serializer.errors, status=status.HTTP_404_NOT_FOUND)

