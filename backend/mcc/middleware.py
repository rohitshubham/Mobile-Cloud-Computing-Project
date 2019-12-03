from django.http import JsonResponse
from firebase_admin import credentials, auth, initialize_app
from django.conf import settings
import os

class RequestAuthenticationMiddleware(object):
    def __init__(self, get_response):
        self.get_response = get_response
        # One-time configuration and initialization.

    def __call__(self, request):
        # Code to be executed for each request before
        # the view (and later middleware) are called.

        response = self.get_response(request)

        # Code to be executed for each request/response after
        # the view is called.

        return response 
           
    def process_view(self, request, view_func, view_args, view_kwargs):        
        if request.path != '/mcc/user/' or request.method != 'POST':
            cred = credentials.Certificate(os.path.join(settings.BASE_DIR, 'key.json'))
            try:
                email = request.headers['X-Authorization-Email']
                uuid = request.headers['X-Authorization-UUID']
            except Exception as e:
                return JsonResponse({"success" : "false", "error" : "RequestHeadersNotFound"}, status=401)
            try:
                user = auth.get_user_by_email(email)
            except Exception as e:
                return JsonResponse({"success" : "false", "error" : "UserNotFound"}, status=401)
            if user.uid != uuid:
                return JsonResponse({"success" : "false", "error" : "InvalidAuthorizationHeader"}, status=401)
