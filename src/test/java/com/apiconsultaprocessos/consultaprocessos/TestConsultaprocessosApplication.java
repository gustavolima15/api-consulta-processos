package com.apiconsultaprocessos.consultaprocessos;

import org.springframework.boot.SpringApplication;

public class TestConsultaprocessosApplication {

	public static void main(String[] args) {
		SpringApplication.from(ConsultaprocessosApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
