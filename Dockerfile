#FROM openjdk:17-jdk-alpine AS builder
#RUN mkdir -p /app/source
#COPY . /app/source
#WORKDIR /app/source
#RUN ./mvnw clean package
#
#
#
#FROM openjdk:17-jdk-alpine
#COPY --from=builder app/source/target/*.jar app/app.jar
#EXPOSE 8080
#ENTRYPOINT ["java","-jar", "app/app.jar"]

#COPY  target/cloneBackend-0.0.1-SNAPSHOT.jar app.jar
#EXPOSE 8080
#ENTRYPOINT ["java","-jar","/app.jar"]
#ARG JAR_FILE=target/cloneBackend-0.0.1-SNAPSHOT.jar
#COPY ${JAR_FILE} application.jar
#EXPOSE 8080
#ENTRYPOINT ["java","-jar","/application.jar"]


FROM openjdk:17-jdk-alpine AS build
COPY . .
#RUN mvn clean package -Pprod -DskipTests
RUN ./mvnw clean package

FROM openjdk:17-jdk-alpine
COPY --from=build /target/cloneBackend-0.0.1-SNAPSHOT.jar demo.jar
# ENV PORT=8080
EXPOSE 8080
ENTRYPOINT ["java","-jar","demo.jar"]
