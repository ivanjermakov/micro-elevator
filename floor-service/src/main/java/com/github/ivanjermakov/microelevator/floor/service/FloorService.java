package com.github.ivanjermakov.microelevator.floor.service;

import com.github.ivanjermakov.microelevator.core.model.FloorOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.FluxProcessor;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.ReplayProcessor;

@Service
public class FloorService {

	private final FluxProcessor<FloorOrder, FloorOrder> floorOrderProcessor;
	private final FluxSink<FloorOrder> sink;

	@Autowired
	public FloorService() {
		floorOrderProcessor = ReplayProcessor.<FloorOrder>create(1).serialize();
		sink = floorOrderProcessor.sink();
	}

	public void nextOrder(FloorOrder order) {
		sink.next(order);
	}

	public FluxProcessor<FloorOrder, FloorOrder> getFloorOrderProcessor() {
		return floorOrderProcessor;
	}

}
