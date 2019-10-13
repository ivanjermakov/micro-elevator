package com.github.ivanjermakov.microelevator.core.model;

import java.util.StringJoiner;

public class FloorOrder {

	private Integer from;
	private Integer to;
	private Boolean accepted;

	public FloorOrder(Integer from, Integer to) {
		this(from, to, false);
	}

	private FloorOrder() {
		accepted = false;
	}

	private FloorOrder(Integer from, Integer to, Boolean accepted) {
		this.from = from;
		this.to = to;
		this.accepted = accepted;
	}

	public Integer getFrom() {
		return from;
	}

	public Integer getTo() {
		return to;
	}

	public Boolean getAccepted() {
		return accepted;
	}

	public void accept() {
		accepted = true;
	}

	@Override
	public String toString() {
		return new StringJoiner(", ", FloorOrder.class.getSimpleName() + "[", "]")
				.add("from=" + from)
				.add("to=" + to)
				.add("isAccepted=" + accepted)
				.toString();
	}

}
