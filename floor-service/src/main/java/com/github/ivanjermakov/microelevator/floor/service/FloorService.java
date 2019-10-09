package com.github.ivanjermakov.microelevator.floor.service;

import com.github.ivanjermakov.microelevator.core.model.FloorOrder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.DirectProcessor;
import reactor.core.publisher.FluxProcessor;
import reactor.core.publisher.FluxSink;

@Service
public class FloorService {

	private final FluxProcessor<FloorOrder, FloorOrder> floorOrderProcessor;
	private final FluxSink<FloorOrder> sink;

	public FloorService() {
		floorOrderProcessor = DirectProcessor.<FloorOrder>create().serialize();
		sink = floorOrderProcessor.sink();
	}

	public void nextOrder(FloorOrder order) {
		sink.next(order);
	}

	public FluxProcessor<FloorOrder, FloorOrder> getFloorOrderProcessor() {
		return floorOrderProcessor;
	}

}
