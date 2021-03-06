from django.urls import path
from rest_framework.urlpatterns import format_suffix_patterns

from . import views

urlpatterns = [
    path('user/', views.user_save, name=''),
    path('user/<str:email_id>', views.user_get, name=""),
    path('project/user/<str:search_path>', views.user_typeahead, name = ""),
    path('project/', views.project_save, name=''),
    path('project/<str:project_id>', views.project_details, name=''),
    path('projects/<str:email_id>', views.projects_list, name=''),
    path('project/report/<str:project_id>',views.generate_report,name=''),
    path('project/search/keywords/',views.search_project_by_keyword,name=''),
    path('project/search/name/',views.search_project_by_name,name=''),
    path('project/attachment/',views.upload_project_attachment,name=''),
    path('project/attachment/<str:project_id>',views.get_all_project_attachment,name=''),
    path('project/image/',views.upload_project_image,name=''),
    path('project/image/<str:project_id>',views.get_all_project_image,name=''),
    path('task/',views.task_save,name=''),
    path('task/member/',views.add_member_to_task ,name=''),
    path('task/complete/', views.set_task_completed, name=''),
    path('tasks/<str:project_id>/<str:email_id>',views.task_retrive,name=''),
    path('token/', views.save_token, name="" )    
]

urlpatterns = format_suffix_patterns(urlpatterns)