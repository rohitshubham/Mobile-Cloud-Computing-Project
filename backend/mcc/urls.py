from django.urls import path
from rest_framework.urlpatterns import format_suffix_patterns

from . import views

urlpatterns = [
    path('user/', views.user_save, name=''),
    path('user/<str:email_id>', views.user_get, name=""),
    path('project/', views.project_save, name=''),
    path('project/<str:project_id>', views.project_details, name=''),
    path('project/remove_member/<str:project_id>', views.remove_team_member, name=''),
    path('projects/<str:email_id>', views.projects_list, name='')
]

urlpatterns = format_suffix_patterns(urlpatterns)