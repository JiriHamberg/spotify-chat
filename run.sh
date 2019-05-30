#!/bin/sh

docker-compose up -d
export $(cat .env | xargs) && ./gradlew bootRun
