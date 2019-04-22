package de.mazdermind.gintercom.matrix;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MatrixApplication {

	public static void main(String[] args) {
		Thread.currentThread().setName("main");
		SpringApplication.run(MatrixApplication.class, args);
	}

}
