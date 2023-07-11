FROM openjdk:17
EXPOSE 8080
ADD target/eca-registration-svc.jar eca-registration-svc.jar
ENTRYPOINT ["java", "-jar", "/eca-registration-svc.jar"]