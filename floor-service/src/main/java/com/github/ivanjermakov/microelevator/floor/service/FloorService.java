package com.github.ivanjermakov.microelevator.floor.service;

import com.github.ivanjermakov.microelevator.core.model.FloorOrder;
import com.github.ivanjermakov.microelevator.core.model.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.ReplayProcessor;

@Service
public class FloorService {

	private final Subject<FloorOrder> floorOrderSubject;

	@Autowired
	public FloorService() {
		floorOrderSubject = new Subject<>(ReplayProcessor.<FloorOrder>create(1).serialize());
	}

	public void nextOrder(FloorOrder order) {
		floorOrderSubject.sink().next(order);
	}

	public Subject<FloorOrder> getFloorOrderSubject() {
		return floorOrderSubject;
	}

}
