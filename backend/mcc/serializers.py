from rest_framework import serializers

from .models import UserAuth, User, Project, UserProject

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
        fields = ['name', 'team_members', 'deadline', 'description', 'requester_email', 'project_type', 'keywords', 'badge', 'creation_time']

class UserProjectSerializer(serializers.HyperlinkedModelSerializer):
    class Meta:
        model = UserProject
        fields = ['email_id', 'project_id', 'is_project_administrator']
