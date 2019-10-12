package com.github.ivanjermakov.microelevator.logic.service;

import com.github.ivanjermakov.microelevator.core.model.ElevatorState;
import com.github.ivanjermakov.microelevator.core.model.FloorOrder;
import com.github.ivanjermakov.microelevator.core.model.Route;
import com.github.ivanjermakov.microelevator.core.model.enums.Status;
import com.github.ivanjermakov.microelevator.core.service.WebClientService;
import com.github.ivanjermakov.microelevator.logic.algorithm.ElevatorRouter;
import com.github.ivanjermakov.microelevator.logic.algorithm.SimpleElevatorRouter;
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
import reactor.util.function.Tuples;

@Service
public class LogicService {

	private static final Logger LOG = LoggerFactory.getLogger(LogicService.class);

	private Flux<FloorOrder> floorOrderFlux;
	private Flux<ElevatorState> elevatorStateFlux;

	private final WebClientService webClientService;
	private final ElevatorRouter router;
	private final FluxProcessor<Route, Route> routeProcessor;
	private final FluxSink<Route> routeSink;

	public LogicService(WebClientService webClientService) {
		this.router = new SimpleElevatorRouter();
		this.routeProcessor = ReplayProcessor.<Route>create(1).serialize();
		this.routeSink = routeProcessor.sink();

		this.webClientService = webClientService;
	}

	public FluxProcessor<Route, Route> getRouteProcessor() {
		return routeProcessor;
	}

	@EventListener(ApplicationReadyEvent.class)
	public void subscribe() {
		floorOrderFlux = webClientService.build(
				webClientService.floorServiceClient(),
				"/subscribe",
				new ParameterizedTypeReference<ServerSentEvent<FloorOrder>>() {}
		);

		elevatorStateFlux = webClientService.build(
				webClientService.elevatorServiceClient(),
				"/subscribe",
				new ParameterizedTypeReference<ServerSentEvent<ElevatorState>>() {}
		);

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

		routeSink.next(nextRoute);
	}

	private void processState(ElevatorState state) {
		LOG.info("processing new state: {}", state);
		Route nextRoute = router.forState(state);

		routeSink.next(nextRoute);
	}

}
