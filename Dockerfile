FROM adoptopenjdk/openjdk11:alpine-jre

EXPOSE 8081

COPY /target/cloudStorage-0.0.1-SNAPSHOT.jar app.jar

CMD ["java", "-jar", "app.jar"]