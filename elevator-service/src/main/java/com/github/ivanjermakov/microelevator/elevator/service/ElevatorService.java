package com.github.ivanjermakov.microelevator.elevator.service;

import com.github.ivanjermakov.microelevator.core.model.ElevatorState;
import com.github.ivanjermakov.microelevator.core.model.Route;
import com.github.ivanjermakov.microelevator.core.model.Subject;
import com.github.ivanjermakov.microelevator.core.model.enums.Status;
import com.github.ivanjermakov.microsubscriber.annotation.Subscribe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.ReplayProcessor;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class ElevatorService {

	private static final Logger LOG = LoggerFactory.getLogger(ElevatorService.class);

	@Subscribe(
			baseUrl = "http://localhost:8081/logic",
			uri = "/route",
			responseType = Route.class
	)
	private Flux<Route> routeFlux;

	private ElevatorState idleState;

	private AtomicReference<Route> route = new AtomicReference<>(new Route());

	private final Subject<ElevatorState> elevatorStateSubject;

	@Autowired
	public ElevatorService() {
		idleState = new ElevatorState(
				Status.IDLE,
				1
		);

		elevatorStateSubject = new Subject<>(ReplayProcessor.<ElevatorState>create(1).serialize());

		LOG.debug("setting initial state: {}", idleState);
		nextState(idleState);

	}

	@EventListener(ApplicationReadyEvent.class)
	public void subscribe() {
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
		elevatorStateSubject.sink().next(state);
	}

	public void processRoute(Integer toFloor) {
		ElevatorState nextState = new ElevatorState(Status.RUNNING, toFloor);
		elevatorStateSubject.sink().next(nextState);

		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		LOG.info("moved to floor: {}", toFloor);
		idleState = new ElevatorState(Status.IDLE, toFloor);
		elevatorStateSubject.sink().next(idleState);
	}

	public Subject<ElevatorState> getElevatorStateSubject() {
		return elevatorStateSubject;
	}

}
