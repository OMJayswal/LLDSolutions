package com.test.strategy;

import com.test.dto.SelectionCriteria;
import com.test.model.Ride;

import java.util.List;

public interface SelectionStrategy {
    public Ride selectRide(List<Ride> rides, SelectionCriteria criteria);
}
