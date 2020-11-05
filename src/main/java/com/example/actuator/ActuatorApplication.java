package com.example.actuator;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.data.annotation.Id;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Stream;

@SpringBootApplication(proxyBeanMethods = false)
public class ActuatorApplication {

	public static void main(String[] args) {
		SpringApplication.run(ActuatorApplication.class, args);
	}
	
    @Bean
    ApplicationRunner init(DatabaseClient dbc, ReservationRepository reservationRepository) {
        return args -> {
			dbc.sql("drop table reservation").fetch().rowsUpdated().onErrorResume(e -> Mono.empty())
			.thenMany(dbc.sql("create table reservation\n" +
				"(\n" +
				"    id   serial primary key,\n" +
				"    name varchar(255) not null\n" +
				")").fetch().rowsUpdated())
			.thenMany(reservationRepository.save(new Reservation(null ,"Andy")))
			.thenMany(reservationRepository.save(new Reservation(null ,"Josh")))
			.subscribe();
			// reservationRepository.findAll().subscribe(System.out::println);
        };
    }

	// @Bean
	// ApplicationListener<ApplicationReadyEvent> ready(ReservationRepository rr) {
	// 	return event -> rr.findAll().subscribe(System.out::println);
	// }
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
