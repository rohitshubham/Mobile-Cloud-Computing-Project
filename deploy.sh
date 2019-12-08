# Frontend

## Requirements
## Installs 
sudo apt-get --quiet update --yes
sudo apt-get --quiet install --yes wget tar unzip lib32stdc++6 lib32z1
sudo wget --quiet --output-document=android-sdk.zip https://dl.google.com/android/repository/sdk-tools-linux-4333796.zip
sudo unzip -d android-sdk-linux android-sdk.zip
sudo echo y | android-sdk-linux/tools/bin/sdkmanager "platforms;android-28" >/dev/null
sudo echo y | android-sdk-linux/tools/bin/sdkmanager "platform-tools" >/dev/null
sudo echo y | android-sdk-linux/tools/bin/sdkmanager "build-tools;28.0.2" >/dev/null
sudo export ANDROID_HOME=$PWD/android-sdk-linux
sudo export PATH=$PATH:$PWD/android-sdk-linux/platform-tools/
sudo chmod +x ./fe/gradlew
sudo set +o pipefail
sudo yes | android-sdk-linux/tools/bin/sdkmanager --licenses
sudo set -o pipefail
## Build
sudo cd ./fe
sudo ./gradlew assembleDebug
cd ..


#Backend
## Requirements

# we need to install cloud SDK and python requirements and docker
echo "deb [signed-by=/usr/share/keyrings/cloud.google.gpg] http://packages.cloud.google.com/apt cloud-sdk main" | sudo tee -a /etc/apt/sources.list.d/google-cloud-sdk.list
sudo curl https://packages.cloud.google.com/apt/doc/apt-key.gpg | sudo apt-key --keyring /usr/share/keyrings/cloud.google.gpg add -
sudo apt-get update && sudo apt-get install google-cloud-sdk

# install docker via convenience script 
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh

cd ./backend


## Add Gcloud permissions to build and deploy our code
gcloud auth activate-service-account --key-file mcc-fall-2019-g14-e5833e504c42.json

# deploy our backend 
# it will build the dockerimage first and hence no separate build is required for our backend
gcloud --quiet --project mcc-fall-2019-g14 app deploy app.yaml

# deploy cloud endpoints
gcloud endpoints services deploy openapi-appengine.yml
cd ..

# create instance for email messaging service using terraform

#install terraform
sudo wget https://releases.hashicorp.com/terraform/0.12.17/terraform_0.12.17_linux_amd64.zip
unzip terraform_0.12.17_linux_amd64.zip
sudo mv terraform /usr/local/bin/
cd terraform

#Create the VM Instance
sudo terraform init 
sudo terraform plan
sudo yes yes | terraform apply

cd ..

# Notifications
## Requirements
cd ./notifications
sudo npm install -g firebase-tools

FIREBASE_TOKEN=1//0cyStsU35tJHYCgYIARAAGAwSNwF-L9Irv1XyJXNu9xyMNf6zhYVuL7yEcYq16Wh-eNe0wUEtIgyoCrYyXdHmn8xYjgMIQ7qWmGM

## Deploy
sudo firebase deploy --only functions --token "$FIREBASE_TOKEN"