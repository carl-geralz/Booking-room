FROM maven:3.9.9-eclipse-temurin-21-alpine AS build
WORKDIR /app
COPY pom.xml /app/
COPY src /app/src
RUN mvn dependency:go-offline && mvn package -DskipTests -DskipCompile

FROM openjdk:21
WORKDIR /app
COPY --from=build /app/target/challenge-booking-room-0.0.1-SNAPSHOT.jar /app/challenge-booking-room.jar
EXPOSE 31337
ENTRYPOINT ["java", "-jar", "challenge-booking-room.jar"]

FROM docker.elastic.co/beats/filebeat:8.15.2
COPY ./devops/filebeat/filebeat.yml /usr/share/filebeat/filebeat.yml
USER root

FROM jenkins/jenkins:2.462.3-jdk17
USER root
RUN apt-get install ca-certificates curl && \
install -m 0755 -d /etc/apt/keyrings && \
curl -fsSL https://download.docker.com/linux/debian/gpg -o /etc/apt/keyrings/docker.asc && \
chmod a+r /etc/apt/keyrings/docker.asc && \
apt-get update && \
echo \
    "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.asc] https://download.docker.com/linux/debian \
    $(. /etc/os-release && echo "$VERSION_CODENAME") stable" | \
    tee /etc/apt/sources.list.d/docker.list > /dev/null && \
apt-get update && \
apt-get install -y lsb-release zip unzip docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin fuse-overlayfs maven openjdk-17-jdk
RUN dockerd &

FROM python:3.9.20-slim-bookworm AS build.trufflehog
RUN apt update && apt install -y git && \
    pip install truffleHog && \
    git config --global --add safe.directory /app && \
    mkdir -p /app/trufflehog_reports
WORKDIR /app
COPY . .
CMD ["sh", "-c", "trufflehog --repo_path . --json https://github.com/carl-geralz/Booking-room > /app/trufflehog_reports/trufflehog_report.json"]