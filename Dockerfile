FROM openjdk:17-jdk-alpine
COPY --from=build /target/cloneBackend-0.0.1-SNAPSHOT.jar app.jar
#COPY  target/cloneBackend-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app.jar"]
#ARG JAR_FILE=target/cloneBackend-0.0.1-SNAPSHOT.jar
#COPY ${JAR_FILE} application.jar
#EXPOSE 8080
#ENTRYPOINT ["java","-jar","/application.jar"]
