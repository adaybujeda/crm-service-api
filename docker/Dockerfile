FROM maven:3-openjdk-11
WORKDIR /app
ADD . .

RUN mvn clean install

CMD ["docker/docker-command-start.sh"]
EXPOSE 8080