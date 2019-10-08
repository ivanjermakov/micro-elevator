package com.github.ivanjermakov.microelevator.logic.controller;

import com.github.ivanjermakov.microelevator.core.model.Route;
import com.github.ivanjermakov.microelevator.logic.service.LogicService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
public class LogicController {

	private final LogicService logicService;

	public LogicController(LogicService logicService) {
		this.logicService = logicService;
	}

	@GetMapping("route")
	public Flux<Route> route() {
		return logicService.getRouteProcessor();
	}

}
