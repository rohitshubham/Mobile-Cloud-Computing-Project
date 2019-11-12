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


