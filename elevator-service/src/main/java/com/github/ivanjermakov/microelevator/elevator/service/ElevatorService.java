package com.github.ivanjermakov.microelevator.elevator.service;

import com.github.ivanjermakov.microelevator.core.model.ElevatorState;
import com.github.ivanjermakov.microelevator.core.model.Route;
import com.github.ivanjermakov.microelevator.core.model.enums.Status;
import com.github.ivanjermakov.microelevator.core.service.WebClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.DirectProcessor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxProcessor;
import reactor.core.publisher.FluxSink;

import java.util.Optional;

@Service
public class ElevatorService {

	private static final Logger LOG = LoggerFactory.getLogger(ElevatorService.class);

	private Flux<Route> routeFlux;
	private ElevatorState idleState;

	private final FluxProcessor<ElevatorState, ElevatorState> elevatorStateProcessor;
	private final FluxSink<ElevatorState> elevatorStateSink;

	private final WebClientService webClientService;

	public ElevatorService(WebClientService webClientService) {
		idleState = new ElevatorState(
				Status.IDLE,
				1
		);

		elevatorStateProcessor = DirectProcessor.<ElevatorState>create().serialize();
		elevatorStateSink = elevatorStateProcessor.sink();

		this.webClientService = webClientService;
	}

	@EventListener(ApplicationReadyEvent.class)
	public void subscribe() {
		routeFlux = webClientService.build(
				webClientService.logicServiceClient(),
				"/route",
				new ParameterizedTypeReference<ServerSentEvent<Route>>() {},
				this::subscribe
		);

		routeFlux.subscribe(this::processRoute);
	}

	public void nextState(ElevatorState state) {
		elevatorStateSink.next(state);
	}

	public void processRoute(Route route) {
		LOG.info("processing route: [{}]", route);

		Optional<Integer> nextFloor = route.next();
		if (nextFloor.isPresent()) {
			LOG.info("moving to floor: {}", nextFloor);
			ElevatorState nextState = new ElevatorState(Status.RUNNING, nextFloor.get());
			elevatorStateSink.next(nextState);

			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			LOG.info("moved to floor: {}", nextFloor);
			idleState = new ElevatorState(Status.IDLE, nextFloor.get());
			elevatorStateSink.next(idleState);
		} else {
			LOG.info("processing route is empty, idling");
			elevatorStateSink.next(idleState);
		}
	}

	public FluxProcessor<ElevatorState, ElevatorState> getElevatorStateProcessor() {
		return elevatorStateProcessor;
	}

}
