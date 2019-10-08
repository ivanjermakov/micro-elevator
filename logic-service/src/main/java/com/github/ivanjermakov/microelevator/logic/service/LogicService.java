package com.github.ivanjermakov.microelevator.logic.service;

import com.github.ivanjermakov.microelevator.core.model.FloorOrder;
import com.github.ivanjermakov.microelevator.core.service.WebClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class LogicService {

	private static final Logger LOG = LoggerFactory.getLogger(LogicService.class);

	private Flux<FloorOrder> floorOrderFlux;

	private final WebClientService webClientService;

	public LogicService(WebClientService webClientService) {
		this.webClientService = webClientService;
	}

	@EventListener(ApplicationReadyEvent.class)
	public void subscribe() {
		floorOrderFlux = webClientService.floorServiceClient()
				.get()
				.uri("/subscribe")
				.retrieve()
				.bodyToFlux(new ParameterizedTypeReference<ServerSentEvent<FloorOrder>>() {})
				.map(ServerSentEvent::data);

		floorOrderFlux.subscribe(this::processOrder);
	}

	private void processOrder(FloorOrder order) {
		LOG.info("processing new order: {}", order);
	}

}
