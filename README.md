
## Requirements

* installed JDK 17

## How to build

```bash
./gradlew clean build
```

## How to run

```bash
java -Xmx8G -XX:+UnlockExperimentalVMOptions -XX:+UseZGC -XX:Tier4CompileThreshold=2 -jar build/libs/demo-0.0.1-SNAPSHOT.jar
```

# How to use

```bash
# upload file
curl -X POST -F "file=@/path/to/file.json" http://localhost:8080/api/v1/upload
# and convert it with SNAPPY compression level
curl -X POST http://localhost:8080/api/v1/convert/SNAPPY/file.json
```


# GraalVM support (WIP)

## How to build native image

```bash
./gradlew bootBuildImage
```

## Hot to run native image with Docker

```bash
docker run --rm -p 8080:8080 docker.io/library/demo:0.0.1-SNAPSHOT
```

# native build with maven (WIP)

```bash
mvn clean -DskipTests -Pnative package native:compile
```