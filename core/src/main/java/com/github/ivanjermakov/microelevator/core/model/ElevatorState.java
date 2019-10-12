package com.github.ivanjermakov.microelevator.core.model;

import com.github.ivanjermakov.microelevator.core.model.enums.Status;

import java.util.StringJoiner;

public class ElevatorState {

	private Status status;
	private Integer floor;

	private ElevatorState() {
	}

	public ElevatorState(Status status, Integer floor) {
		this.status = status;
		this.floor = floor;
	}

	public Status getStatus() {
		return status;
	}

	public Integer getFloor() {
		return floor;
	}

	@Override
	public String toString() {
		return new StringJoiner(", ", ElevatorState.class.getSimpleName() + "[", "]")
				.add("status=" + status)
				.add("floor=" + floor)
				.toString();
	}

}
