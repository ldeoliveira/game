FROM openjdk:8-alpine

# Required for starting application up.
RUN apk update && apk add bash

RUN mkdir -p /opt/app
ENV PROJECT_HOME /opt/app

COPY build/libs/game-0.0.1-SNAPSHOT.jar $PROJECT_HOME/game-0.0.1-SNAPSHOT.jar

WORKDIR $PROJECT_HOME

CMD ["java", "-Dspring.data.mongodb.uri=mongodb://mongo-db:27017/game","-Djava.security.egd=file:/dev/./urandom","-jar","./game-0.0.1-SNAPSHOT.jar"]