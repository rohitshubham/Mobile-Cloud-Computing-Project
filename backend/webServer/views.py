import pyrebase 
from django.shortcuts import render 

config = {
    'apiKey': "AIzaSyAZwRL3Oz-rJ3vx9U_IBrzqUDF2EcAj4kk",
    'authDomain': "test-mcc-bba43.firebaseapp.com",
    'databaseURL': "https://test-mcc-bba43.firebaseio.com",
    'projectId': "test-mcc-bba43",
    'storageBucket': "test-mcc-bba43.appspot.com",
    'messagingSenderId': "162360953834",
    'appId': "1:162360953834:web:b345e4c8a6f5d3489cc093"

}

firebase = pyrebase.initialize_app(config)
auth = firebase.auth()

def singIn(request):
    return render(request, "signIn.html")
def postsign(request):
    email=request.POST.get('email')
    passw = request.POST.get("pass")
    try:
        user = auth.create_user_with_email_and_password(email, passw)
        
        #user = auth.sign_in_with_email_and_password(email,passw)
        print(user)
    except:
        message = "invalid cerediantials"
        #print(auth.currentUser.getIdToken())
    return render(request,"signIn.html",{"msg":message})
    print(user)
    return render(request, "welcome.html",{"e":email})
