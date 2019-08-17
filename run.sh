#!/bin/sh

docker-compose build spotify-chat-api
docker-compose up
#export $(cat .env | xargs) && ./gradlew bootRun
