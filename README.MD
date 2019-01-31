# ShugunDB Backend

This the the official ShogunDB Project. 

## Backend

The backend is a Spring Boot REST Server which handles all database operations. All endpoints are documented in the 
![wiki](https://github.com/BerryPlay/ShogunDB/wiki).

## Frontend

The frontend is a standalone Vue.js application which communicates over HTTP requests with the backend.

# Requirements

All you need is an installed openjdk11 (which you can download ![here](https://jdk.java.net/11/)). Please check, if you
have the right version by opening a terminal (or windows cmd) and typing `java -version`. The output should be something
like:

```bash
openjdk version "11.0.1" 2018-10-16
OpenJDK Runtime Environment (build 11.0.1+13)
OpenJDK 64-Bit Server VM (build 11.0.1+13, mixed mode)
```

## Installation

### Configure the application

First you have to setup the configuration. The simplest way is to rename the `application.example.yaml` in the 
`backend/src/main/resources/` directory to `application.yaml` and insert the correct database information.

There are two types of databases you can use: MySQL and H2. To setup the MsSQL connection, insert the following settings
into the `application.yaml` (change all fields marked with `<...>`):

```yaml
spring:
  jpa:
    database: MYSQL
  datasource:
    url: jdbc:mysql://<mysql-server-address>/<database-name>
    username: <username>
    password: <password>
```

If you instead want to use a single database file with H2, insert (this will create and use a h2 database file):

```yaml
spring:
  jpa:
    database: H2
    show-sql: false
    generate-ddl: true
    hibernate:
      ddl-auto: update
  datasource:
    url: jdbc:h2:file:<path to database file>
    username: <username>
    password: <password>
    driver-class-name: org.h2.Driver
```

### Build the application

To build the application just execute `./mvnw install` (or `mvnw.cmd install` if you are on windows). This will 
build the frontend, merge it into the public folder of the backend and finally build the backend. The `.jar` archive 
will be placed under `target/backend-X.X.X.jar` and can be executed with `java -jar target/shogundb-X.X.X.jar`. The 
configuration can be changed at any time by placing the `application.yaml` file in the same directory as the 
`shogundb-0.1.0.jar`.