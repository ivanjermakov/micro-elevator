package com.github.ivanjermakov.microelevator.core.model;

import java.util.StringJoiner;

public class FloorOrder {

	private Integer from;
	private Integer to;
	private Boolean isAccepted;

	public FloorOrder(Integer from, Integer to) {
		this(from, to, false);
	}

	private FloorOrder() {
		isAccepted = false;
	}

	private FloorOrder(Integer from, Integer to, Boolean isAccepted) {
		this.from = from;
		this.to = to;
		this.isAccepted = isAccepted;
	}

	public Integer getFrom() {
		return from;
	}

	public Integer getTo() {
		return to;
	}

	public Boolean getAccepted() {
		return isAccepted;
	}

	public void accept() {
		isAccepted = true;
	}

	@Override
	public String toString() {
		return new StringJoiner(", ", FloorOrder.class.getSimpleName() + "[", "]")
				.add("from=" + from)
				.add("to=" + to)
				.add("isAccepted=" + isAccepted)
				.toString();
	}

}
