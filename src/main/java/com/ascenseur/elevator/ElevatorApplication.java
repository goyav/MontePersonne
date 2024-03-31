package com.ascenseur.elevator;

import com.ascenseur.elevator.modele.Elevator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;

@EnableAsync
@SpringBootApplication
public class ElevatorApplication {

	public static void main(String[] args) {
		SpringApplication.run(ElevatorApplication.class, args);
	}
}
