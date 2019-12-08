from firebase_admin import credentials, initialize_app, db, storage, firestore
from mailjet_rest import Client

from datetime import datetime
import time
import os

api_key = '37013bebf24cd060688f439bb3d5972c'
api_secret = 'b7fe21248957ace76cfbe2e5b1a53987'
mailjet = Client(auth=(api_key, api_secret), version='v3.1')


cred = credentials.Certificate('key.json')

default_app = initialize_app(cred,{
    'storageBucket': 'mcc-fall-2019-g14.appspot.com'
})

db = firestore.client()

def send_mail_to_users(task_id, deadline, name):
    userTasks = db.collection('userTasks').where(u'task_id', u'==', task_id).stream()
    try:
        for userTask in userTasks:
            task_val = userTask.to_dict()
            send_mail(task_val["email_id"], name, deadline)
    except Exception as e:
            print(e)




def find_and_mail():
    tasks = db.collection(u'tasks').stream()
    for task in tasks:
        task_id= task.id
        task_val = task.to_dict()
        try:
            deadline = task_val["deadline"]

            deadline_date = datetime.strptime(deadline, "%Y-%m-%dT%H:%M:%S")

            if (datetime.now() - deadline_date).days == 2 or (datetime.now() - deadline_date).days == 1:
                if task_val["status"] is not "On-going" or task_val["status"] is not "O": 
                    send_mail_to_users(task_id, task_val["deadline"], task_val["name"])
        except Exception as e:
            print(e)

while True:
    find_and_mail()
    time.sleep(24*60*60)


def send_mail(email_id, name, deadline):
    data = {
    'Messages': [
        {
        "From": {
            "Email": f"rohit.raj@aalto.fi",
            "Name": "MCC-Group14 Email Service"
        },
        "To": [
            {
            "Email": f"{email_id}",
            "Name": f"{email_id}"
            }
        ],
        "Subject": "Your Task Deadline.",
        "HTMLPart": f"<h3>Dear {email_id},</h3> <br/> Your task named : <b>{name}</b> is expiring on <b>{deadline}</b>",
        "CustomID": "AppGettingStartedTest"
        }
    ]
    }
    result = mailjet.send.create(data=data)
    print(result.status_code)
    print(result.json())