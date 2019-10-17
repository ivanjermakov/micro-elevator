package com.github.ivanjermakov.microelevator.core.model;

import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxProcessor;
import reactor.core.publisher.FluxSink;

public class Subject<T> {

	private final FluxProcessor<T, T> processor;
	private final FluxSink<T> sink;

	public Subject(FluxProcessor<T, T> processor) {
		this.processor = processor;
		this.sink = processor.sink();
	}

	public FluxSink<T> sink() {
		return sink;
	}

	public Flux<T> flux() {
		return processor;
	}

}
