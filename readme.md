# Cloud-based task management project – Group 14

The application is a project management cloud platform. It provides with following functionalities:

* project creation/deletion;
* project report generation;
* task creation/deletion for a project;
* attach multi-format files to project (`txt`, `pdf`, `jpg`, `mp3`);
* adding members to a project;
* user settings and account management;
* notifications on project assignment.
 

---

There are following basic components of this application:

- _Backend_ - Runs in on a Dockerized environment in `GCP App Engine` Flex environment.
- _Frontend_ - Android Application targeting API v28.
- _Database_ - The database is hosted on GCP `Firestore Database`.
- _Storage_ - The files are stored in GCP `Cloud buckets`.
- _API Endpoints_ - The OpenAPI Endpoints are deployed on `Google Cloud Endpoints` using a ESP to our App engine.
- _Push Notification Service_ - Triggered by `Google Cloud Functions` and messages sent to the device via `Firebase Cloud Messaging`.
- _Email-Notification Service_ - Runs in `Google cloud platform compute engine` (Instance Provisioned by `Terraform`).
- _CI and CD of repo_ - The continuos integration and deployment of the git repo is handled by two private GitLab CI runners. (Hosted on GCP Compute engine and Oracle Cloud engine each).

## Deployment 

The file `deploy.sh` can be used to build, deploy, provision the instances. For deployment on the CI/CD on GitLabCI has been implemented, for more detail see  [pipelines](https://version.aalto.fi/gitlab/CS-E4100/mcc-fall-2019-g14/pipelines). 


<!-- Add here be deployment instructions -->


## Structure

The project consists of four folders in the root: `backend`, `frontend` (`fe`), `terraform` and `notifications`, as described below. 

### Backend

The backend of our project is build using `Django Framework` for Python. We are using a micro-services architecture pattern for a decoupled frontend and backend, and hence for backend communication, we are using REST Framework. The endpoints are designed as per OpenAPI 3.0/Swagger specifications.

The backend is deployed as a container in Google Cloud Platform's App Engine Flexible Environment.The base URL of our project is : 
> [https://mcc-fall-2019-g14.appspot.com/mcc/](https://mcc-fall-2019-g14.appspot.com/mcc/).

For a improved compatibility in build deployment and portability, the backend is dockerized into a docker containers using `Ubuntu-18.04` as a base images as specified in the [dockerfile](./backend/Dockerfile). 



The backend folder contains the following files and folders.

```
├── app.yaml
├── Dockerfile
├── env
├── key.json
├── manage.py
├── mcc
├── openapi-appengine.yml
├── requirements.txt
├── templates
└── webServer
```

This [file](backend/key.json) contains the credentials to Firebase. We communicate and perform the request to the back end as a `firebase-admin` user. And hence `firebase-admin` package for python has been used in our code.


<!-- Add libraries -->
#### Libraries 
The main libraries of our backend application is 
* `gunicorn:20.0.0`
* `requests:2.22.0`
* `urllib3:1.25.7` 
* `django:2.2.7`
* `djangorestframework:3.10.3`
* `pyrebase:3.0.27`
* `pillow:6.2.1`
* `firebase-admin:3.2.0`
* `xhtml2pdf:0.2.3`
* `python-dateutil:2.6.1`

<!-- ================================================  -->

### API Reference Guide 

The backend follows the OpenAPI specifications for serving the requests. The API's are deployed on Google cloud Platform's `Cloud Endpoints`. The developer console and the API reference material for all the backend endpoints are in `https://endpointsportal.mcc-fall-2019-g14.cloud.goog/`. 

### Notifications

The push notifications to the device is being sent by the Firebase Cloud Messaging (FCM) service. We can send notifications to every device that has signed-up in the application. To send messages to the device, we use HTTPv1 Protocol specified for the Firebase Cloud Messaging Service. 

To send the notifications, we are using serverless `Google Cloud Functions`. The code for this functionality has been written in NodeJs and is in `sendNotifications/functions/index.js`. The trigger that we have used is HTTP Trigger i.e. we can trigger the function through HTTP post requests. The url endpoint for our function is : https://us-central1-mcc-fall-2019-g14.cloudfunctions.net/sendNotification . The triggers in our application happen when there are there are members added to the project or tasks is assigned to a project.



More details(request/response) about this can be seen in the OpenApi reference under the `tokens` tag.

### Email Notification:
The email notification is sent via MailJet CLient running on a Google cloud platform's compute engine. The code run as a python script on this infrastructure. This infrastructure can be automatically deployed using Hashicorp's Terraform. It deploys a Ubuntu instance with `g1-micro` setting and installs the necessary dependencies using the `email_service_init.sh`.

The `email_job.py` then runs on this instance and sends email notification to the user when the deadline is approaching.


### Frontend

The frontend has been developed using both Kotlin and Java. You can find the application logic in the [folder](fe/app/src/main/java/mcc/group14/apiclientapp), 

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

#### CI/CD using GitLabCI

For Continuous integration and deployment, we are using our own private Gitlab runners. There are two runners. One is hosted on Google Cloud Compute Engine and the other on Oracle Cloud Platform. The gitlab Ci code can be seen in the file : `.gitlab-ci.yml`  

There are two stages to our CI/CD pipeline: `build` and `deploy`. The build is responsible for building the code and `deploy` stage deploys it to gcloud in case of backend and creates downloadable artifacts in case of frontend. 

Each of the build and deploy is divided into two parts: The `front-end` and the `backend`. When the code is pushed to `master`, both the build and deploy runs. That is, the current code is deployed to the server and downloadable artifacts are created. 

In cases of push to other branch or a merge request, only the build pipeline is triggered and hence the code is build but not deployed to the server.  