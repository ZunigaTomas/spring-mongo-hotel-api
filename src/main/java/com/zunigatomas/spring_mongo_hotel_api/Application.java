package com.zunigatomas.spring_mongo_hotel_api;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		// Cargar el archivo .env
		Dotenv dotenv = Dotenv.configure().load();

		// Chequear y mostrar la clave de acceso S3 desde el .env
		String accessKey = dotenv.get("AWS_S3_ACCESS_KEY");
		System.out.println("AWS S3 Access Key from .env: " + accessKey);

		// Iniciar la aplicación Spring Boot
		SpringApplication.run(Application.class, args);
	}
}