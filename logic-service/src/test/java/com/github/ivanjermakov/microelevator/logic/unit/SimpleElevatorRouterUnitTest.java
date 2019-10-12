package com.github.ivanjermakov.microelevator.logic.unit;

import com.github.ivanjermakov.microelevator.core.model.ElevatorState;
import com.github.ivanjermakov.microelevator.core.model.FloorOrder;
import com.github.ivanjermakov.microelevator.core.model.Route;
import com.github.ivanjermakov.microelevator.core.model.enums.Status;
import com.github.ivanjermakov.microelevator.logic.algorithm.SimpleElevatorRouter;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;

public class SimpleElevatorRouterUnitTest {

	private SimpleElevatorRouter router = new SimpleElevatorRouter();
	private ElevatorState idleState;

	@Before
	public void before() {
		idleState = new ElevatorState(Status.IDLE, 1);
	}

	@Test
	public void shouldRouteEmptyOrder() {
		Route route = router.forState(idleState);

		assertEquals(Collections.emptyList(), route.getFloors());
	}

	@Test
	public void shouldRouteFrom2to5() {
		Route route = router.route(idleState, new FloorOrder(2, 5));

		assertEquals(Arrays.asList(2, 5), route.getFloors());
	}

	@Test
	public void shouldRouteFrom1to5() {
		Route route = router.route(idleState, new FloorOrder(1, 5));

		assertEquals(Collections.singletonList(5), route.getFloors());
	}

	@Test
	public void shouldRouteFrom5to1() {
		Route route = router.route(idleState, new FloorOrder(5, 1));

		assertEquals(Arrays.asList(5, 1), route.getFloors());
	}

	@Test(expected = IllegalStateException.class)
	public void shouldThrowException_WhenRouteFromAcceptedOrder() {
		FloorOrder order = new FloorOrder(1, 2);
		order.accept();
		router.route(idleState, order);
	}

	@Test(expected = IllegalStateException.class)
	public void shouldThrowException_WhenRouteFromRunningState() {
		router.route(new ElevatorState(Status.RUNNING, 1), new FloorOrder(1, 5));
	}

}
