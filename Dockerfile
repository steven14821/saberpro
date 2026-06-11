FROM eclipse-temurin:21-jre
COPY "./target/SABERPRO-1.jar" "app.jar"
EXPOSE 8118
ENTRYPOINT ["java", "-jar", "app.jar"]