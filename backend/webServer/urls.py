from django.contrib import admin
from django.urls import path, include

urlpatterns = [
    path('project/', include('mcc.urls')),
    path('admin/', admin.site.urls),
]
