#!/bin/bash

echo "---------------------------------------------------------------------"
echo "Rebuild posBuddy-Core"
echo "Step 1 -> pull from Github"
git pull
echo "Step 2 -> build App with gradle"
./gradlew build
echo "Step 3 -> build Docker Image with gradle"
./gradlew dockerBuild
echo "Step 4 -> Restart Docker image"
docker-compose up -d -f /opt/posBuddy/docker-compose.yml
echo "---------------------------------------------------------------------"

