# Same minimal, no-build-tooling style as practice/docker-fundamentals/Dockerfile --
# one precompiled class, one JDK base image, zero dependencies.
FROM eclipse-temurin:21-jre

WORKDIR /app

COPY src/ApiServer.class .

EXPOSE 8080

CMD ["java", "ApiServer"]
