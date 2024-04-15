FROM registry.access.redhat.com/ubi8/openjdk-17:latest
COPY build/libs/*.jar /app/app.jar
WORKDIR /app/
ENTRYPOINT ["sh","-c","java -XX:+UseContainerSupport -XX:MaxRAMPercentage=75 $JAVA_OPTS -jar app.jar"]