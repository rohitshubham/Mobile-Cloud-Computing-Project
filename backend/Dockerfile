FROM ubuntu:18.04

RUN apt-get update 
    
RUN apt-get install -y python3 python3-pip

ENV LANG C.UTF-8

ADD requirements.txt /app/
WORKDIR /app
RUN pip3 install -r requirements.txt
   
ADD . /app

EXPOSE 8080

ENTRYPOINT gunicorn -b :8080 webServer.wsgi