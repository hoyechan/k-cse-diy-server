#!/bin/bash
./gradlew clean build -x test
sudo docker compose up -d --build
