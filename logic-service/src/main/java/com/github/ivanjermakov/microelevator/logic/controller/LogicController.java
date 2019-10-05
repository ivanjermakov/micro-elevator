package com.github.ivanjermakov.microelevator.logic.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.List;

@RestController
public class LogicController {

	@GetMapping("route")
	public Flux<List<Integer>> route() {
		return null;
	}

}
