package com.github.ivanjermakov.microelevator.elevator.service;

import com.github.ivanjermakov.microelevator.core.model.ElevatorState;
import com.github.ivanjermakov.microelevator.core.service.WebClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@Service
public class ElevatorService {

	private static final Logger LOG = LoggerFactory.getLogger(ElevatorService.class);

	private final List<FluxSink<ElevatorState>> subscriptions = new CopyOnWriteArrayList<>();
	private Flux<List<Integer>> routeFlux;

	private final WebClientService webClientService;

	public ElevatorService(WebClientService webClientService) {
		this.webClientService = webClientService;
	}

	@EventListener(ApplicationReadyEvent.class)
	public void subscribe() {
		routeFlux = webClientService.logicServiceClient()
				.get()
				.uri("/route")
				.retrieve()
				.bodyToFlux(new ParameterizedTypeReference<ServerSentEvent<List<Integer>>>() {})
				.map(ServerSentEvent::data);

		routeFlux.subscribe(this::processRoute);
	}

	public void newState(ElevatorState state) {
		subscriptions.forEach(s -> s.next(state));
	}

	public void connect(FluxSink<ElevatorState> sink) {
		subscriptions.add(sink);
	}

	public void processRoute(List<Integer> integers) {
		LOG.info("processing route: [{}]", integers
				.stream()
				.map(Objects::toString)
				.collect(Collectors.joining(", ")));
	}

}
