package com.github.ivanjermakov.microelevator.logic.service;

import com.github.ivanjermakov.microelevator.core.model.ElevatorState;
import com.github.ivanjermakov.microelevator.core.model.FloorOrder;
import com.github.ivanjermakov.microelevator.core.model.Route;
import com.github.ivanjermakov.microelevator.core.model.Subject;
import com.github.ivanjermakov.microelevator.core.model.enums.Status;
import com.github.ivanjermakov.microelevator.logic.algorithm.ElevatorRouter;
import com.github.ivanjermakov.microsubscriber.annotation.Subscribe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.ReplayProcessor;
import reactor.util.function.Tuples;

@Service
public class LogicService {

	private static final Logger LOG = LoggerFactory.getLogger(LogicService.class);

	@Subscribe(
			baseUrl = "http://localhost:8083/floor",
			uri = "/subscribe",
			responseType = FloorOrder.class
	)
	private Flux<FloorOrder> floorOrderFlux;

	@Subscribe(
			baseUrl = "http://localhost:8082/elevator",
			uri = "/subscribe",
			responseType = ElevatorState.class
	)
	private Flux<ElevatorState> elevatorStateFlux;

	private final Subject<Route> routeSubject;

	private final ElevatorRouter router;

	@Autowired
	public LogicService(ElevatorRouter router) {
		routeSubject = new Subject<>(ReplayProcessor.<Route>create(1).serialize());

		this.router = router;
	}

	public Subject<Route> getRouteSubject() {
		return routeSubject;
	}

	@EventListener(ApplicationReadyEvent.class)
	public void subscribe() {
		floorOrderFlux
				.withLatestFrom(
						elevatorStateFlux
								.filter(es -> es.getStatus().equals(Status.IDLE)),
						Tuples::of
				)
				.subscribe(tuple -> processOrder(tuple.getT1(), tuple.getT2()));

		elevatorStateFlux
				.filter(es -> es.getStatus().equals(Status.IDLE))
				.subscribe(this::processState);
	}

	private void processOrder(FloorOrder order, ElevatorState state) {
		LOG.info("processing new order: {} with state: {}", order, state);
		Route nextRoute = router.route(state, order);

		routeSubject.sink().next(nextRoute);
	}

	private void processState(ElevatorState state) {
		LOG.info("processing new state: {}", state);
		Route nextRoute = router.forState(state);

		routeSubject.sink().next(nextRoute);
	}

}
