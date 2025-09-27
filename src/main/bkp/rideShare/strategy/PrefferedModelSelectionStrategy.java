package com.test.strategy;

import com.test.dto.SelectionCriteria;
import com.test.model.Ride;

import java.util.ArrayList;
import java.util.List;

public class PrefferedModelSelectionStrategy implements SelectionStrategy{
    public Ride selectRide(List<Ride> rideList, SelectionCriteria selectionCriteria){
        List<Ride> resultList = new ArrayList<>();
        Ride result = null;
        for(Ride ride:rideList){
          if(ride.getVehicle().getVehicleModel().equals(selectionCriteria.getModel())){
              resultList.add(ride);
          }
        }
        Integer seatsAvailable = -1;
        for(Ride ride:resultList){
            if(ride.getSeatsAvailable()> seatsAvailable){
                seatsAvailable = ride.getSeatsAvailable();
                result = ride;
            }
        }
        return result;
    }
}
