package com.github.ivanjermakov.microelevator.core.model;

import java.util.List;

public class Route {

	private List<Integer> floors;

	private Route() {
	}

	public Route(List<Integer> floors) {
		this.floors = floors;
	}

	public List<Integer> getFloors() {
		return floors;
	}

}
