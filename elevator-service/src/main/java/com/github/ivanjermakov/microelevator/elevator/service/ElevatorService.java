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
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxProcessor;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.ReplayProcessor;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class ElevatorService {

	private static final Logger LOG = LoggerFactory.getLogger(ElevatorService.class);

	private Flux<Route> routeFlux;
	private ElevatorState idleState;

	private AtomicReference<Route> route = new AtomicReference<>(new Route());

	private final FluxProcessor<ElevatorState, ElevatorState> elevatorStateProcessor;
	private final FluxSink<ElevatorState> elevatorStateSink;

	private final WebClientService webClientService;

	public ElevatorService(WebClientService webClientService) {
		idleState = new ElevatorState(
				Status.IDLE,
				1
		);

		elevatorStateProcessor = ReplayProcessor.<ElevatorState>create(1).serialize();
		elevatorStateSink = elevatorStateProcessor.sink();

		LOG.debug("setting initial state: {}", idleState);
		nextState(idleState);

		this.webClientService = webClientService;
	}

	@EventListener(ApplicationReadyEvent.class)
	public void subscribe() {
		routeFlux = webClientService.build(
				webClientService.logicServiceClient(),
				"/route",
				new ParameterizedTypeReference<ServerSentEvent<Route>>() {}
		);

		routeFlux.subscribe(r -> {
			this.route.set(r);

			LOG.info("received route: {}", route);

			Optional<Integer> nextFloor = r.next();

			if (nextFloor.isPresent()) {
				LOG.info("moving to floor: {}", nextFloor.get());
				processRoute(nextFloor.get());
			} else {
				LOG.info("processing route is empty, idling");
			}
		});
	}

	public void nextState(ElevatorState state) {
		elevatorStateSink.next(state);
	}

	public void processRoute(Integer toFloor) {
		ElevatorState nextState = new ElevatorState(Status.RUNNING, toFloor);
		elevatorStateSink.next(nextState);

		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		LOG.info("moved to floor: {}", toFloor);
		idleState = new ElevatorState(Status.IDLE, toFloor);
		elevatorStateSink.next(idleState);
	}

	public FluxProcessor<ElevatorState, ElevatorState> getElevatorStateProcessor() {
		return elevatorStateProcessor;
	}

}
