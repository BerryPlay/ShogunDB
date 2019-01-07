# ShugunDB Backend

This the the official ShogunDB Project. 

## Backend

The backend is a Spring Boot REST Server which handles all database operations. All endpoints are documented in the 
![Project-Wiki](https://github.com/BerryPlay/ShogunDB/wiki).

## Frontend

The frontend is a standalone Vue.js application which communicates over HTTP requests with the backend.

## Installation

### With maven

To build the application just execute `./mvnw install`. This will build the frontend, merge it into the public folder of the backend and finally build the backend. The `.jar` archive will be placed under `backend/target/backend-X.X.X-SNAPSHOT.jar` and can be executed with `java -jar backend/target/backend-X.X.X-SNAPSHOT.jar`.

### With Docker

To build the docker image, just run:
> mvn install dockerfile:build

You can run a docker container with the following command:
> docker run -p 8080:8080 --name "shogundb" -i spring/shogundb:latest
