# Audio Storage Project

## Description

The Audio Storage project is a test task that accepts audio files, converts them to an internal format `.wav`, and stores them. Audio files can be uploaded and retrieved using `curl` commands.

## Functionality

### Uploading an Audio File

You can upload an audio file using the following `curl` command:

```sh
curl --request POST 'http://localhost:8080/audio/user/1/phrase/1' --form 'audio_file=@./sample3.m4a'
```
The file will be converted to the internal format .wav and stored on the server.
Retrieving an Audio File

You can retrieve an audio file using the following curl command:
```sh
curl --request GET 'http://localhost:8080/audio/user/1/phrase/1/m4a' -o './test_response_file_1_1.m4a'
```
The file will be converted to the requested format and downloaded.
Supported Formats

The following formats are supported as part of the test task:
* .m4a
* .mp3
* .wav

You can add support for more formats supported by ffmpeg by editing the AudioFormat enum.

## Installation and Running
Requirements
* Java 17
* Maven
* Docker
* ffmpeg

### Building the Project
1. Clone the repository:
```sh
git clone <URL of your repository> cd audio-storage
```
2. Build the project using Maven:
```sh
mvn clean package
```

### Running with Docker
1. Build the Docker image:
```sh
docker build -t audio-storage .
```
2. Run the Docker container:
```sh
docker run -p 8080:8080 audio-storage
```
### Configurations for local run
application.properties contains need to be checked before local sturtup. For local development and running `dev` profile is recomended.
* audio.storage.path: Specifies the path where audio files will be stored.
```
audio.storage.path=/app/audio_storage
```
* ffmpeg.path: Specifies the path to the ffmpeg executable.
```
ffmpeg.path=/usr/bin/ffmpeg
```
* ffprobe.path: Specifies the path to the ffprobe executable.
```
ffprobe.path=/usr/bin/ffprobe
```
