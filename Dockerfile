FROM openjdk:21-slim
WORKDIR /app

COPY target/rinha-backend-proxy-*.jar app.jar

ENTRYPOINT ["sh", "-c", "java -XX:+UseParallelGC -XX:ActiveProcessorCount=8 -XX:MaxRAMPercentage=50 -jar /app/app.jar"]