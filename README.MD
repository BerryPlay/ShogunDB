# ShugunDB Backend

The REST(full), java spring boot based, backend server of the shogundb project.

## Installation

### With Docker

To build the docker image, just run:
> mvn install dockerfile:build

You can run a docker container with the following command:
> docker run -p 8080:8080 --name "shogundb" -i spring/shogundb:latest