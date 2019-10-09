package com.github.ivanjermakov.microelevator.logic.service;

import com.github.ivanjermakov.microelevator.core.model.ElevatorState;
import com.github.ivanjermakov.microelevator.core.model.FloorOrder;
import com.github.ivanjermakov.microelevator.core.model.Route;
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
import reactor.core.publisher.DirectProcessor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxProcessor;
import reactor.core.publisher.FluxSink;

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
		this.routeProcessor = DirectProcessor.<Route>create().serialize();
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
		floorOrderFlux.subscribe(fo -> LOG.debug("received order: {}", fo));

		elevatorStateFlux = webClientService.build(
				webClientService.elevatorServiceClient(),
				"/subscribe",
				new ParameterizedTypeReference<ServerSentEvent<ElevatorState>>() {}
		);
		elevatorStateFlux.subscribe(es -> LOG.debug("received state: {}", es));

		Flux
				.zip(
						elevatorStateFlux,
						floorOrderFlux
				)
				.subscribe(tuple -> processOrder(tuple.getT1(), tuple.getT2()));
	}

	private void processOrder(ElevatorState state, FloorOrder order) {
		LOG.info("processing new order: {}", order);
		Route nextRoute = router.route(state, order);

		routeSink.next(nextRoute);
	}

}
