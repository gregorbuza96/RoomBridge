FROM amazoncorretto:17-alpine
WORKDIR /app
COPY target/booking-service-1.0.0.jar app.jar
EXPOSE 8083
ENTRYPOINT ["java", "-jar", "app.jar"]
