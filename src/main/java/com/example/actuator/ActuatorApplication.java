package com.example.actuator;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.data.annotation.Id;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.Collections;
import java.util.Map;

@SpringBootApplication(proxyBeanMethods = false)
public class ActuatorApplication {

	public static void main(String[] args) {
		SpringApplication.run(ActuatorApplication.class, args);
	}

	@Bean
	ApplicationListener<ApplicationReadyEvent> ready(ReservationRepository rr) {
		return event -> {
			rr.findAll().subscribe(System.out::println);
		};
	}
}

interface ReservationRepository extends ReactiveCrudRepository<Reservation, Integer> {
}

@Data
@AllArgsConstructor
@NoArgsConstructor
class Reservation {

	@Id
	private Integer id;
	private String name;

}

@RestController
@RequiredArgsConstructor
class ReservationController {

	private final ReservationRepository reservationRepository;

	@GetMapping("/reservations")
	Flux<Reservation> reservations() {
		return this.reservationRepository.findAll();
	}
}

@RestController
class GreetingsController {

	@GetMapping("/")
	public Map<String, String> greet() {
		return Collections.singletonMap("greetings", "Olá!");
	}
}
