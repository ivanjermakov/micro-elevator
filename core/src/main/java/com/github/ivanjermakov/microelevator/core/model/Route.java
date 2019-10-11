package com.github.ivanjermakov.microelevator.core.model;

import java.util.List;
import java.util.Optional;

public class Route {

	private List<Integer> floors;

	private Route() {
	}

	public Route(List<Integer> floors) {
		this.floors = floors;
	}

	public Optional<Integer> next() {
		return floors
				.stream()
				.findFirst();
	}

	public List<Integer> getFloors() {
		return floors;
	}

	@Override
	public String toString() {
		return "Route{" +
				"floors=" + floors +
				'}';
	}

}
