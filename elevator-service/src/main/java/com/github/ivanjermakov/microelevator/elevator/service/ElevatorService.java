package com.github.ivanjermakov.microelevator.elevator.service;

import com.github.ivanjermakov.microelevator.core.model.ElevatorState;
import org.springframework.stereotype.Service;
import reactor.core.publisher.FluxSink;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class ElevatorService {

	private final List<FluxSink<ElevatorState>> subscriptions = new CopyOnWriteArrayList<>();

	public void newState(ElevatorState state) {
		subscriptions.forEach(s -> s.next(state));
	}

	public void connect(FluxSink<ElevatorState> sink) {
		subscriptions.add(sink);
	}

}
