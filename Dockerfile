FROM openjdk:17
LABEL authors="aditya.singh"
#VOLUME /tmp
WORKDIR /app
COPY ./build/libs/TinyUrl-0.0.1-SNAPSHOT.jar /app/app.jar
ENV SPRING_SERVER_PORT=9000
EXPOSE 9000
#ARG JAR_FILE=target/spring-boot-docker.jar
#ADD ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","app.jar"]