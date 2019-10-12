package com.github.ivanjermakov.microelevator.logic.algorithm;

import com.github.ivanjermakov.microelevator.core.model.ElevatorState;
import com.github.ivanjermakov.microelevator.core.model.FloorOrder;
import com.github.ivanjermakov.microelevator.core.model.Route;
import com.github.ivanjermakov.microelevator.core.model.enums.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class SimpleElevatorRouter implements ElevatorRouter {

	private static final Logger LOG = LoggerFactory.getLogger(SimpleElevatorRouter.class);

	private List<FloorOrder> orders = new ArrayList<>();

	@Override
	public Route route(ElevatorState state, FloorOrder order) {
		if (!state.getStatus().equals(Status.IDLE))
			throw new IllegalStateException("it is only possible to build route for idling elevator");
		if (order.getAccepted())
			throw new IllegalStateException("cannot route accepted order");

//		add current order to list
		orders.add(order);

		return build(state);
	}

	@Override
	public Route forState(ElevatorState state) {
		return build(state);
	}

	private Route build(ElevatorState state) {
//		complete order if it is accepted and came to the destination
		orders.removeIf(o -> o.getAccepted() && o.getTo().equals(state.getFloor()));

//		accept orders, coming on current floor (including current)
		orders
				.stream()
				.filter(o -> o.getFrom().equals(state.getFloor()))
				.forEach(FloorOrder::accept);

		LOG.info("building route for orders: {}", orders);

		Route completeRoute = new Route(
				orders
						.stream()
						.flatMap(o -> Stream.of(o.getFrom(), o.getTo()))
						.collect(Collectors.toList())
		);

//		remove first if it is the same as current floor
		if (completeRoute.next().isPresent() && completeRoute.next().get().equals(state.getFloor())) {
			completeRoute.getFloors().remove(0);
		}

		LOG.info("complete route: {}", completeRoute);

		return completeRoute;
	}

}
