# A deliberately minimal, real Dockerfile -- every instruction here is
# used in the chapter's own explanation of what each one does.
FROM eclipse-temurin:21-jre

# Everything from here runs relative to this directory inside the image.
WORKDIR /app

# Copy only the one compiled class this image needs -- not the whole
# build context -- keeping the resulting image small.
COPY app/Server.class .

# The port the application listens on. This does NOT publish the port to
# the host by itself -- it's documentation read by `docker run -p` and
# by anyone reading the Dockerfile, not an access-control mechanism.
EXPOSE 8080

# The command that runs when a container starts FROM this image.
CMD ["java", "Server"]
