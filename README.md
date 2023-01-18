# Project Birdnest

My solution to Reaktor's Developer Trainee, summer 2023 [pre-assignment](https://assignments.reaktor.com/birdnest).

# Summary

The application consists of two parts, server (Back-end) and client (Front-end).

The server has scheduled a task that polls Reaktor's API every 2 seconds. It then determines the violating drones and
drones that have violations within 10 minutes. Those violations get pushed to a Redis database with an expiry of 10
minutes. The Redis database will automatically drop the violations if they are not renewed within that
10 minutes.

The server also exposes a WebSocket-based RSocket endpoint that sends the client all saved violations and then sends
events about violations added/updated or removed. These events are backed by Redis keyspace notifications that are
converted to messages.

The client is a simple SPA that subscribes to the RSocket stream and display the information to the user.

# Technologies

- [Gradle](https://gradle.org/): Build tool
- [RSocket](https://rsocket.io/): Reactive Streams Application protocol
- [Redis](https://redis.io/): In-memory data store

### Client

- [Vue 3](https://vuejs.org/): JavaScript UI Framework
- [Vuetify](https://next.vuetifyjs.com/): Material Design Component Framework
- [io-ts](https://gcanti.github.io/io-ts/): Runtime I/O type system

### Server

- [Kotlin](https://kotlinlang.org/): Programming language
- [Spring Boot 3](https://spring.io/projects/spring-boot): Spring application framework
- [Project Reactor](https://projectreactor.io/): Reactive programming library for Java
- [Google Jib](https://github.com/GoogleContainerTools/jib): Java container build tool
- ["Distroless" Container Images](https://github.com/GoogleContainerTools/distroless): Minimal container base images

### Deployment

- [Docker Compose](https://docs.docker.com/compose/): Multi-container orchestration tool
- [Nginx Proxy Manager](https://nginxproxymanager.com/): Easy [Nginx](https://www.nginx.com/)-based reverse proxy
  management

# Building

### Requirements

- Java Development Kit (JDK) 17+

### Setup

Clone the repository to your desired location and enter the folder

```shell
git clone https://github.com/ChargedByte/reaktor-summer-2023.git && cd reaktor-summer-2023
```

On UNIX, you may need to make the Gradle Wrapper script executable

```shell
chmod +x gradlew
```

## JAR file

Build by running the `assemble` Gradle task

```shell
./gradlew assemble
```

You can find the Jar file at `server/build/libs/server-<version>.jar`

## Docker container image

Build by running the `jibDockerBuild` or the `jibBuildTar`

```shell
./gradlew jibDockerBuild
# or
./gradlew jibBuildTar
```

With `jibDockerBuild` the image is added to docker directory
If using `jibBuildTar` the image file is located at `server/build/jib-image.tar` and can be loaded
with `docker load --input jib-image.tar`.

## OCI container image

Build by running the `jibBuildTar` with the `` argument

```shell
./gradlew jibBuildTar -Djib.container.format="OCI"
```

You can find the image file located at `server/build/jib-image.tar`

# License

This project is licensed under the [MIT License](LICENSE).
