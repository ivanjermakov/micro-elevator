package com.github.ivanjermakov.microelevator.core.model;

import com.github.ivanjermakov.microelevator.core.model.enums.Status;

import java.util.List;

public class ElevatorState {

	private Status status;
	private List<FloorOrder> orders;
	private List<Integer> route;

	private ElevatorState() {
	}

	public ElevatorState(Status status, List<FloorOrder> orders, List<Integer> route) {
		this.status = status;
		this.orders = orders;
		this.route = route;
	}

	public Status getStatus() {
		return status;
	}

	public List<FloorOrder> getOrders() {
		return orders;
	}

	public List<Integer> getRoute() {
		return route;
	}

}
