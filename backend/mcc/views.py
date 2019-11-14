from rest_framework import status
from rest_framework.decorators import api_view
from rest_framework.response import Response
from .serializers import UserAuthSerializer,UserSerializer
from .models import UserAuth,User
from django.views.decorators.csrf import csrf_exempt
import firebase_admin
from firebase_admin import credentials
from firebase_admin import db,auth
from django.http import HttpResponse



