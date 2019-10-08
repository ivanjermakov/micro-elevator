package com.github.ivanjermakov.microelevator.logic.algorithm;

import com.github.ivanjermakov.microelevator.core.model.ElevatorState;
import com.github.ivanjermakov.microelevator.core.model.FloorOrder;
import com.github.ivanjermakov.microelevator.core.model.Route;

/**
 * Simple elevator router.
 * With every new floor order place it's from and to floors into route.
 */
public class SimpleElevatorRouter implements ElevatorRouter {

	@Override
	public Route route(ElevatorState state, FloorOrder order) {
		return null;
	}

}
