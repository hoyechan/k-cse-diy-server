#!/bin/bash
./gradlew clean build -x test
docker compose up -d --build
