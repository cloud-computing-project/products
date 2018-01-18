FROM openjdk:8-jre-alpine

RUN mkdir /app

WORKDIR /app

ADD ./api/target/products-api-1.0.0-SNAPSHOT.jar /app

EXPOSE 8090

CMD ["java", "-jar", "products-api-1.0.0-SNAPSHOT.jar"]