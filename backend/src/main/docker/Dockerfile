# FROM frolvlad/alpine-oraclejdk8:slim
# smaller image, builds and runs fine, not tested
FROM openjdk:8-jre-alpine
VOLUME /tmp
ADD backend-0.0.1.jar app.jar
RUN sh -c 'touch /app.jar'
ENV JAVA_OPTS=""
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -Dspring.profiles.active=docker -jar /app.jar" ]