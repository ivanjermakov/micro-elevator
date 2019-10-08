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

import java.util.concurrent.TimeUnit;

@Service
public class LogicService {

	private static final Logger LOG = LoggerFactory.getLogger(LogicService.class);

	private Flux<FloorOrder> floorOrderFlux;
	private Flux<ElevatorState> elevatorStateFlux;

	private final WebClientService webClientService;
	private final ElevatorRouter router;
	private final FluxProcessor<Route, Route> routeProcessor;

	public LogicService(WebClientService webClientService) {
		this.router = new SimpleElevatorRouter();
		this.routeProcessor = DirectProcessor.<Route>create().serialize();

		this.webClientService = webClientService;
	}

	@EventListener(ApplicationReadyEvent.class)
	public void subscribe() {
		LOG.info("subscribing to floor/subscribe");
		floorOrderFlux = webClientService.floorServiceClient()
				.get()
				.uri("/subscribe")
				.retrieve()
				.bodyToFlux(new ParameterizedTypeReference<ServerSentEvent<FloorOrder>>() {})
				.map(ServerSentEvent::data)
				.doOnError(e -> {
					LOG.error("error subscribing to floor/subscribe; retrying", e);
					try {
						TimeUnit.MILLISECONDS.sleep(webClientService.reconnectionTimeout);
					} catch (InterruptedException ignored) {
					}
					subscribe();
				});

		LOG.info("subscribing to elevator/subscribe");
		elevatorStateFlux = webClientService.elevatorServiceClient()
				.get()
				.uri("/subscribe")
				.retrieve()
				.bodyToFlux(new ParameterizedTypeReference<ServerSentEvent<ElevatorState>>() {})
				.map(ServerSentEvent::data)
				.doOnError(e -> {
					LOG.error("error subscribing to elevator/subscribe; retrying;", e);
					try {
						TimeUnit.MILLISECONDS.sleep(webClientService.reconnectionTimeout);
					} catch (InterruptedException ignored) {
					}
					subscribe();
				});

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

		routeProcessor.sink().next(nextRoute);
	}

	public FluxProcessor<Route, Route> getRouteProcessor() {
		return routeProcessor;
	}

}
