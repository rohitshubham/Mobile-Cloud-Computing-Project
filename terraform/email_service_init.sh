#!/bin/bash

#install required stuff for running our code
apt -get update && apt-get install -y python3 python3-pip tmux
pip3 install mailjet-rest
pip3 install firebase-admin

