Spring Boot project with Spring WebFlux and Spring Boot actuators.

The `application.properties` is:

```
spring.main.cloud-platform=kubernetes
management.endpoint.shutdown.enabled=true
management.endpoints.web.exposure.include=shutdown,health,info
```

# Building the app

To build the application there are options:

## Build with the compile script

```
./compile.sh
```

## Build with maven (no script)

```
mvn -Pnative clean package
```

## Build with Spring container image support using buildpacks

```
mvn spring-boot:build-image
```

# Launching the app

If building without container image support the executable is in the target folder:

```
./target/webflux-actuator-graalvm
```

If building as a container image the `docker-compose.yml` can be used to launch it:

```
docker-compose up webflux-actuator-graal
```


To build and run the native application packaged in a lightweight container:
```
./gradlew bootBuildImage
docker-compose up
```

# Testing it out

```
curl http://localhost:8080/actuator/health
{"groups":["liveness","readiness"],"status":"UP"}

curl localhost:8080/actuator/health/readiness
{"status":"UP"}

curl localhost:8080/actuator/health/liveness
{"status":"UP"}

curl -X POST http://localhost:8080/actuator/shutdown
{"message":"Shutting down, bye..."}
```
