from django.urls import path
from rest_framework.urlpatterns import format_suffix_patterns

from . import views

urlpatterns = [
    path('user/', views.user_save, name=''),
    path('user/<str:email_id>', views.user_get, name=""),
    path('project/', views.project_save, name=''),
    path('project/<str:project_id>', views.project_details, name=''),
    path('projects/<str:email_id>', views.projects_list, name=''),
    path('task/',views.task_save,name=''),
    path('task/member/',views.add_member_to_task ,name='')
]

urlpatterns = format_suffix_patterns(urlpatterns)