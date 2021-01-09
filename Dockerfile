FROM openjdk:11-jdk
WORKDIR /app
ADD target/crm-service-api-1.0.jar crm-service-api-1.0.jar

ENV DB_URL "jdbc:h2:/app/crm-service-db"

RUN java -jar crm-service-api-1.0.jar db migrate /config.yml
RUN java -jar crm-service-api-1.0.jar create-admin-user -p changeme /config.yml
CMD ["java", "-jar", "crm-service-api-1.0.jar", "server", "/config.yml"]
EXPOSE 8080