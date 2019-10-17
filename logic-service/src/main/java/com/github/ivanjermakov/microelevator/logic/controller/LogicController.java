package com.github.ivanjermakov.microelevator.logic.controller;

import com.github.ivanjermakov.microelevator.core.model.Route;
import com.github.ivanjermakov.microelevator.logic.service.LogicService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
public class LogicController {

	private static final Logger LOG = LoggerFactory.getLogger(LogicController.class);

	private final LogicService logicService;

	public LogicController(LogicService logicService) {
		this.logicService = logicService;
	}

	@GetMapping(path = "route", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<Route> route() {
		return logicService.getRouteSubject().flux();
	}

}
