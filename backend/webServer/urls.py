from django.contrib import admin
from django.urls import path, include

urlpatterns = [
    path('mcc/', include('mcc.urls')),
    path('admin/', admin.site.urls),
]
