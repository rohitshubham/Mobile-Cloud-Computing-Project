# Cloud-based task management project – Group 14

The application is a project management cloud platform. It provides with following functionalities:

* project creation/deletion;
* project report generation;
* task creation/deletion for a project;
* attach multi-format files to project (`txt`, `pdf`, `jpg`, `mp3`);
* adding members to a project;
* user settings and account management;
* notifications on project assignment.

<!-- ============ might be missing something: =================== -->
 
<!--  * conversion of an image to a project -->
<!--  * project search -->
<!--  * admin/user project privileges -->
 

## Deployment 

For deployment the CI/CD on GitLab has been implemented, for more detail see  [pipelines](https://version.aalto.fi/gitlab/CS-E4100/mcc-fall-2019-g14/pipelines). 


<!-- Add here be deployment instructions -->


## Structure

The project consists of three folders in the root: `backend`, `frontend` (`fe`) and `notifications`, as described below. 

### Backend

The backend folder contains the following files and folders.

```
├── app.yaml
├── Dockerfile
├── email_job.py
├── env
├── key.json
├── manage.py
├── mcc
├── openapi-appengine.yml
├── requirements.txt
├── templates
└── webServer
```

Backend is deployed in docker containers using `Ubuntu-18.04` docker images as specified in the [dockerfile](./backend/Dockerfile). The framework we use is Django, the main logic is in  [webServer](./backend/webServer). 

For more information about the services offered by the backend see [openapi-appengine.yml](./backed/openapi-appengine.yml).

<!-- ========================================================== -->

<!-- Rohit: please modify here -->


<!-- Insert important files -->
This [file](backend/key.json) contains the credentials to Firebase.

<!-- Are they important? I am not sure -->
.vscode - the launching project endpoint (??? does it)
env - the GCP deployng settings 



<!-- Add libraries -->
#### Libraries 


<!-- ================================================  -->

### Notifications


### Frontend

The frontend has been developed using both Kotlin and Java. You can find the application logic in this [folder](fe/app/src/main/java/mcc/group14/apiclientapp), 

```
.
├── api
├── data
├── utils
└── views
```

* [api](./fe/app/src/main/java/mcc/group14/apiclientapp/api):  classes and interfaces used to contact the backend;
* [data](./fe/app/src/main/java/mcc/group14/apiclientapp/data) data models, used to send the current state of the application to the server;
* [utils](./fe/app/src/main/java/mcc/group14/apiclientapp/utils): utility classes, used by other components;
* [views](./fe/app/src/main/java/mcc/group14/apiclientapp/views): UI components logic.

The graphical interface components (layouts, pictures, icons, etc.) can be found in the [res folder](./fe/app/src/main/res), following the usual conventions for [android applications](https://developer.android.com/studio/projects).

#### Libraries

As shown in [build.gradle](fe/app/build.gradle), our app target SDK is 28 Android API (9.0 Pie). The minimum SDK API supported is 26. 

Below a list of used libraries:

* firebase:
	* auth: `firebase-auth:17.0.0`;
	* database: `firebase-database:17.0.0`;
	* messaging: `firebase-messaging:18.0.0`.

* api: 
	* calls to BE: `retrofit:2.3.0`;
  <!-- ================ Kibria which picasso version??? ================ -->
  
  <!-- 	  * picasso: `picasso:<version>`  -->

  
* Android libraries: `support-v7:28.0.0`.

* UI:
	* theme: `nachos:1.1.1`;
	* toasts: `md-toast:0.9.0`.

