from firebase_admin import credentials, initialize_app, auth


cred = credentials.Certificate('key.json')
default_app = initialize_app(cred)
try:
    user = auth.list_users().iterate_all()
    x = next((x for x in user if x.display_name == value), None)
except auth.EmailAlreadyExistsError:
    print("user already exists")

# user = auth.get_user_by_email('rohit@example1.com')

