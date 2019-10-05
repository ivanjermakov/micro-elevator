package com.github.ivanjermakov.microelevator.floor.service;

import com.github.ivanjermakov.microelevator.core.model.FloorOrder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.FluxSink;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class FloorService {

	private final List<FluxSink<FloorOrder>> subscriptions = new CopyOnWriteArrayList<>();

	public void newOrder(FloorOrder order) {
		subscriptions.forEach(s -> s.next(order));
	}

	public void connect(FluxSink<FloorOrder> sink) {
		subscriptions.add(sink);
	}

}
