# Usar la imagen oficial de OpenJDK 21
FROM openjdk:21-jdk-slim

# Establecer el directorio de trabajo dentro del contenedor
WORKDIR /app

# Copiar el archivo JAR generado en el contenedor
COPY target/spring-mongo-hotel-api-0.0.1-SNAPSHOT.jar /app/spring-mongo-hotel-api.jar

# Exponer el puerto en el que correrá la aplicación
EXPOSE 4040

# Comando para ejecutar la aplicación Spring Boot
ENTRYPOINT ["java", "-jar", "spring-mongo-hotel-api.jar"]
