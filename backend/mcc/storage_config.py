# Import the Firebase service

import firebase_admin
from firebase_admin import credentials
from firebase_admin import db,auth,storage


# Initialize the default app


cred = credentials.Certificate('/home/kibria/MCC/MCCPROJECT/test-mcc-bba43-firebase-adminsdk-1icxf-088bb1f3a5.json')
firebase_admin.initialize_app(cred, {
    'storageBucket': 'test-mcc-bba43.appspot.com'
})

bucket = storage.bucket()



def upload_blob(source_file_name, destination_blob_name):
  

    #Directory structure is allowed as well
    blob = bucket.blob(destination_blob_name)

    blob.upload_from_filename(source_file_name)

    print('File {} uploaded to {}.'.format(
        source_file_name,
        destination_blob_name))
    
    print(blob.public_url)



upload_blob('/home/kibria/new.log','some.log')