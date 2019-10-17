package com.github.ivanjermakov.microelevator.elevator.controller;

import com.github.ivanjermakov.microelevator.core.model.ElevatorState;
import com.github.ivanjermakov.microelevator.elevator.service.ElevatorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
public class ElevatorController {

	private static final Logger LOG = LoggerFactory.getLogger(ElevatorController.class);

	private final ElevatorService elevatorService;

	public ElevatorController(ElevatorService elevatorService) {
		this.elevatorService = elevatorService;
	}

	@GetMapping(path = "subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<ElevatorState> state() {
		return elevatorService.getElevatorStateSubject().flux();
	}

}
