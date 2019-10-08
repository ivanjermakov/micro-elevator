package com.github.ivanjermakov.microelevator.floor.service;

import com.github.ivanjermakov.microelevator.core.model.FloorOrder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.DirectProcessor;
import reactor.core.publisher.FluxProcessor;

@Service
public class FloorService {

	private FluxProcessor<FloorOrder, FloorOrder> floorOrderProcessor;

	public FloorService() {
		floorOrderProcessor = DirectProcessor.<FloorOrder>create().serialize();
	}

	public void nextOrder(FloorOrder order) {
		floorOrderProcessor.sink().next(order);
	}

	public FluxProcessor<FloorOrder, FloorOrder> getFloorOrderProcessor() {
		return floorOrderProcessor;
	}

}
