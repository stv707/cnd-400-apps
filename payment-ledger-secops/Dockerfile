# Intentionally insecure legacy base image for Module 7 (Ex 7.2 Trivy CVE gate exercise).
# Harden to a minimal JRE runtime (e.g. eclipse-temurin:21-jre-alpine) with a non-root user.
FROM openjdk:17-jdk-slim-bullseye
WORKDIR /app
COPY target/payment-ledger-secops-*.jar /app/app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
