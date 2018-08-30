FROM openjdk:8-jdk-alpine
VOLUME /tmp
COPY libs/nack.jar app.jar

ADD java-agent.zip /tmp/
RUN mkdir -p /appdynamics/ && \
    unzip -oq /tmp/java-agent.zip -d /appdynamics/ && \
    rm /tmp/java-agent.zip

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-javaagent:/appdynamics/java-agent/javaagent.jar","-jar","/app.jar"]