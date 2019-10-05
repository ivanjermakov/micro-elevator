package com.github.ivanjermakov.microelevator.floor.controller;

import com.github.ivanjermakov.microelevator.core.model.FloorOrder;
import com.github.ivanjermakov.microelevator.floor.service.FloorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
public class FloorController {

	private static final Logger LOG = LoggerFactory.getLogger(FloorController.class);

	private final FloorService floorService;

	public FloorController(FloorService floorService) {
		this.floorService = floorService;
	}

	@PostMapping("order")
	public void order(@RequestBody FloorOrder order) {
		LOG.info("new order: " + order);
		floorService.newOrder(order);
	}

	@GetMapping(path = "subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<FloorOrder> subscribe() {
		LOG.debug("new subscriber");
		return Flux.create(floorService::connect);
	}

}
