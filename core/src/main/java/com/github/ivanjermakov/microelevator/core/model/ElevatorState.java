package com.github.ivanjermakov.microelevator.core.model;

import com.github.ivanjermakov.microelevator.core.model.enums.Status;

public class ElevatorState {

	private Status status;
	private Integer currentFloor;

	private ElevatorState() {
	}

	public ElevatorState(Status status, Integer currentFloor) {
		this.status = status;
		this.currentFloor = currentFloor;
	}

	public Status getStatus() {
		return status;
	}

	public Integer getCurrentFloor() {
		return currentFloor;
	}

}
