package de.mazdermind.gintercom.matrix;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@ComponentScan("de.mazdermind.gintercom")
@PropertySource(value = "git.properties", ignoreResourceNotFound = true)
@EnableScheduling
public class MatrixApplication {

	public static void main(String[] args) {
		Thread.currentThread().setName("main");
		SpringApplication.run(MatrixApplication.class, args);
	}

}
