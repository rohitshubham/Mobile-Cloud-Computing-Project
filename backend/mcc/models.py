from django.db import models

# Create your models here.
# models.py
class UserAuth(models.Model):
    email = models.CharField(max_length=60)
    password = models.CharField(max_length=60)    
    def __str__(self):
        return self.email

class User(models.Model):
    email = models.CharField(max_length=60)
    displayName = models.CharField(max_length=60, unique=True)
    profilePicture = models.ImageField()
    def __str__(self):
        return self.displayName


class Project(models.Model):
    PROJECT_TYPES = [
        ('P', 'Personal'),
        ('T', 'Team')
    ]   
    name = models.CharField(max_length=100)
    team_members = models.TextField()
    deadline = models.DateField()
    description = models.TextField()
    requester_email = models.EmailField()
    project_type = models.CharField(max_length= 1, choices= PROJECT_TYPES, default= 'P')
    keywords = models.TextField()
    badge = models.TextField() #Base64 image or if the badge is fixed, only save the badge URL.
    creation_time = models.DateTimeField()

    def to_dict(self):
        my_dict = {
            u'name' : str(self.name).decode("utf-8"),
            u'team_members' : str(self.team_members).decode("utf-8"),
            u'deadline' : str(self.deadline).decode("utf-8"),
            u'description' : str(self.description).decode("utf-8"),
            u'requester_email' : str(self.requester_email).decode("utf-8"),
            u'project_type' : str(self.project_type).decode("utf-8"),
            u'keywords' : str(self.keywords).decode("utf-8"),
            u'badge' : str(self.badge).decode("utf-8"),
            u'creation_time' : str(self.creation_time).decode("utf-8")
        }
        return my_dict


class UserProject(models.Model):
    email_id = models.EmailField()
    project_id = models.TextField()
    is_project_administrator = models.BooleanField()


class Task(models.Model):
    STATUS_ENUM = [
        ('P', 'Pending'),
        ('O', 'On-Going'),
        ('C', 'Complete')
    ]   
    description = models.TextField()   
    deadline = models.DateField()
    project_id = models.TextField()    
    status = models.CharField(max_length= 1, choices= STATUS_ENUM, default= 'P')
    

   
