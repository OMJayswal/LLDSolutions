package com.test.model;

import com.test.dto.RideStatus;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Ride {
    private String id;
    private final User user;
    private final String source;
    private final String destination;
    private Integer seatsAvailable;
    private List<Booking> rides = new ArrayList<>();
    private final Vehicle vehicle;
    private RideStatus rideStatus;

    public Ride(User user, String source, String destination,Vehicle vehicle,Integer seatsAvailable) {
        this.user = user;
        this.source = source;
        this.destination = destination;
        this.vehicle = vehicle;
        this.seatsAvailable = seatsAvailable;
        this.rideStatus = RideStatus.CREATED;
    }

}
