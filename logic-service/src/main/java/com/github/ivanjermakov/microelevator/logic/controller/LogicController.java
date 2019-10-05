package com.github.ivanjermakov.microelevator.logic.controller;

import com.github.ivanjermakov.microelevator.core.model.FloorOrder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.List;

@RestController
public class LogicController {

	@GetMapping("state")
	public Flux<List<FloorOrder>> state() {
		return null;
	}

}
