package com.github.ivanjermakov.microelevator.core.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;

public class Route {

	private List<Integer> floors;

	public Route() {
		floors = new ArrayList<>();
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
		return new StringJoiner(", ", Route.class.getSimpleName() + "[", "]")
				.add("floors=" + floors)
				.toString();
	}

}
