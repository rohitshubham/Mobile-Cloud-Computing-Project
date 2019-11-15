from django.urls import path
from rest_framework.urlpatterns import format_suffix_patterns

from . import views

urlpatterns = [
    path('user/', views.user_save, name=''),
    path('user/<str:email_id>', views.user_get, name=""),
    path('project/', views.project_save, name='')
]

urlpatterns = format_suffix_patterns(urlpatterns)