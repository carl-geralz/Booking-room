FROM maven:3.9.9-eclipse-temurin-21-alpine AS build
WORKDIR /app
COPY pom.xml /app/
COPY src /app/src
RUN mvn dependency:resolve && mvn package -DskipTests -DskipCompile
FROM openjdk:21
WORKDIR /app
COPY --from=build /app/target/challenge-booking-room-0.0.1-SNAPSHOT.jar /app/challenge-booking-room.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "challenge-booking-room.jar"]