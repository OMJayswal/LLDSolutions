package com.test.strategy;

import com.test.dto.SelectionCriteria;
import com.test.model.Ride;

import java.util.List;

public class MostVacantSelectionStrategy implements SelectionStrategy{
    public Ride selectRide(List<Ride> rideList, SelectionCriteria selectionCriteria){
        Ride result = null;
        Integer seatsAvailable = -1;
        for(Ride ride:rideList){
          if(ride.getSeatsAvailable()> seatsAvailable){
              seatsAvailable = ride.getSeatsAvailable();
              result = ride;
          }
       }
        return result;
    }
}
