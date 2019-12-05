from rest_framework import serializers

from .models import UserAuth, User, Project, UserProject, Task, UserTask

class UserAuthSerializer(serializers.HyperlinkedModelSerializer):
    class Meta:
        model = UserAuth
        fields = ['email', 'password']


class UserSerializer(serializers.HyperlinkedModelSerializer):
    class Meta:
        model = User
        fields = ['email', 'displayName','profilePicture']

class ProjectSerializer(serializers.HyperlinkedModelSerializer):
    
    class Meta:
        model = Project 
        deadline = serializers.DateTimeField(allow_null=True)        
        description = serializers.CharField(max_length=None, min_length=None, allow_null=True) #Why are these charfield and not text fields?
        badge = serializers.CharField(max_length=None, min_length=None, allow_null=True)
        keywords = serializers.CharField(max_length=None, min_length=None, allow_null=True)      
        fields = ['name', 'team_members', 'deadline', 'description', 'requester_email', 'project_type', 'keywords', 'badge', 'creation_time']
        

class UserProjectSerializer(serializers.HyperlinkedModelSerializer):
    class Meta:
        model = UserProject
        fields = ['email_id', 'project_id', 'is_project_administrator']

class TaskSerializer(serializers.HyperlinkedModelSerializer):
    class Meta:
        model = Task
        fields = ['name', 'description', 'deadline', 'project_id', 'status' , 'creation_time']

class UserTaskSerializer(serializers.HyperlinkedModelSerializer):
    class Meta:
        model = UserTask
        fields = ['email_id', 'task_id', 'project_id']
