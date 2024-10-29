# Use the official OpenJDK image as the base image
FROM openjdk:17-jdk-slim

# Install ffmpeg and ffprobe
RUN apt-get update && apt-get install -y ffmpeg

# Set the working directory
WORKDIR /app

# Copy the Maven build file into the container
COPY target/audio-service-1.0.0.jar /app/audio-service.jar

# Specify the command to run the application
ENTRYPOINT ["java", "-jar", "/app/audio-service.jar"]
