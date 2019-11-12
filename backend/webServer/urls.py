from django.contrib import admin
<<<<<<< HEAD
from django.conf.urls import url 
from . import views
urlpatterns = [
    url(r'^admin/', admin.site.urls),
    url(r'^$',views.singIn),
    url(r'^postsign/',views.postsign),
]
=======
from django.urls import path, include

urlpatterns = [
    path('project/', include('mcc.urls')),
    path('admin/', admin.site.urls),
]
>>>>>>> origin/master
