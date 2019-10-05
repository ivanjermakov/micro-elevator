package com.github.ivanjermakov.microelevator.core.model;

public class FloorOrder {

	private Integer from;
	private Integer to;

	private FloorOrder() {
	}

	public FloorOrder(Integer from, Integer to) {
		this.from = from;
		this.to = to;
	}

	public Integer getFrom() {
		return from;
	}

	public Integer getTo() {
		return to;
	}

	@Override
	public String toString() {
		return "FloorOrder{" +
				"from=" + from +
				", to=" + to +
				'}';
	}

}
