package com.example.demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.io.File;

@Slf4j
@SpringBootApplication(proxyBeanMethods = false)
public class App {

	public static void main(String[] args) {
		SpringApplication.run(App.class, args);
	}

	@Bean
	CommandLineRunner clr(@Value("${storage.input}") String input,
						  @Value("${storage.output}") String output) {
		return args -> {
			var in = new File(input);
			if (!in.exists()) {
				in.mkdirs();
			}

			var out = new File(output);
			if (!out.exists()) {
				out.mkdirs();
			}
		};
	}

}
