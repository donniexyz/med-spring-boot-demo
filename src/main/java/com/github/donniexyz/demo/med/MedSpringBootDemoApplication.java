package com.github.donniexyz.demo.med;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan
@EntityScan
@EnableJpaRepositories
public class MedSpringBootDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(MedSpringBootDemoApplication.class, args);
	}

}
