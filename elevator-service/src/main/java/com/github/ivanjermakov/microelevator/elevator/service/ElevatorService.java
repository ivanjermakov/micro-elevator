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

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class ElevatorService {

	private static final Logger LOG = LoggerFactory.getLogger(ElevatorService.class);

	private FluxProcessor<ElevatorState, ElevatorState> elevatorStateProcessor;
	private Flux<Route> routeFlux;
	private ElevatorState idleState;

	private final WebClientService webClientService;

	public ElevatorService(WebClientService webClientService) {
		elevatorStateProcessor = DirectProcessor.<ElevatorState>create().serialize();
		idleState = new ElevatorState(
				Status.IDLE,
				1
		);
		this.webClientService = webClientService;
	}

	@EventListener(ApplicationReadyEvent.class)
	public void subscribe() {
		LOG.info("subscribing to logic/route");
		routeFlux = webClientService.logicServiceClient()
				.get()
				.uri("/route")
				.retrieve()
				.bodyToFlux(new ParameterizedTypeReference<ServerSentEvent<Route>>() {})
				.map(ServerSentEvent::data)
				.doOnError(e -> {
					LOG.error("error subscribing to logic/route; retrying", e);
					try {
						TimeUnit.MILLISECONDS.sleep(webClientService.reconnectionTimeout);
					} catch (InterruptedException ignored) {
					}
					subscribe();
				});

		routeFlux.subscribe(this::processRoute);
	}

	public void nextState(ElevatorState state) {
		elevatorStateProcessor.sink().next(state);
	}

	public void processRoute(Route route) {
		LOG.info("processing route: [{}]", route);

		Optional<Integer> nextFloor = route.next();
		if (nextFloor.isPresent()) {
			LOG.info("moving to floor: {}", nextFloor);
			ElevatorState nextState = new ElevatorState(Status.RUNNING, nextFloor.get());
			elevatorStateProcessor.sink().next(nextState);

			try {
				TimeUnit.MILLISECONDS.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			LOG.info("moved to floor: {}", nextFloor);
			idleState = new ElevatorState(Status.IDLE, nextFloor.get());
			elevatorStateProcessor.sink().next(idleState);
		} else {
			LOG.info("processing route is empty, idling");
			elevatorStateProcessor.sink().next(idleState);
		}
	}

	public FluxProcessor<ElevatorState, ElevatorState> getElevatorStateProcessor() {
		return elevatorStateProcessor;
	}

}
