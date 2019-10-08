package com.github.ivanjermakov.microelevator.logic.algorithm;

import com.github.ivanjermakov.microelevator.core.model.ElevatorState;
import com.github.ivanjermakov.microelevator.core.model.FloorOrder;
import com.github.ivanjermakov.microelevator.core.model.Route;

public interface ElevatorRouter {

	Route route(ElevatorState state, FloorOrder order);

}
