package com.test.service;


import com.test.dto.BookingStatus;
import com.test.dto.RideStatus;
import com.test.dto.SelectionCriteria;
import com.test.model.Booking;
import com.test.model.Ride;
import com.test.model.User;
import com.test.model.Vehicle;
import com.test.strategy.MostVacantSelectionStrategy;
import com.test.strategy.PrefferedModelSelectionStrategy;
import com.test.strategy.SelectionStrategy;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class RideShareService {
    AtomicInteger rId = new AtomicInteger(0);
    AtomicInteger bId = new AtomicInteger(0);

    private UserService userService;
    private VehicleService vehicleService;
    private final Map<String, Ride> rideMap = new HashMap<>();
    private final Map<Map.Entry<String,String>, Ride> rideOffers = new HashMap<>();
    private final Map<String, SelectionStrategy> selectionStrategyMap = new HashMap<>();
    private final Map<String, Booking> bookingMap = new HashMap<>();

    public RideShareService(UserService userService,VehicleService vehicleService){
        this.userService = userService;
        this.vehicleService = vehicleService;
        selectionStrategyMap.put("Most Vacant",new  MostVacantSelectionStrategy());
        selectionStrategyMap.put("Preferred Vehicle",new PrefferedModelSelectionStrategy());
    }

    public Ride offerRide(String username, String origin, Integer availableSeats, String vehicleModel, String vehicleNumber, String destination)  {
        User user  = userService.getUser(username);
        if(user == null){
            System.out.println("User not found");
            return null;
        }
        Vehicle vehicle = vehicleService.getVehicle(vehicleNumber);
        if(vehicle == null){
           System.out.println("Vehicle Not Found");
           return null;
        }
        if(!vehicle.getVehicleModel().equals(vehicleModel)){
            System.out.println("Vehicle with model not found");
            return null;
        }
        Ride ride = new Ride(user,origin,destination,vehicle,availableSeats);
        String rID= String.valueOf(rId.addAndGet(1));
        ride.setId(rID);
        rideMap.put(rID,ride);
        Map.Entry<String,String> entry = Map.entry(user.getName(),vehicle.getVehicleNumber());
        if(rideOffers.containsKey(entry)){
            System.out.println("Offer Already Exists for given user and vehicle");
            return null;
        }
        rideOffers.put(entry,ride);
        return ride;
    }

    public Booking selectRide(String username, String origin, String destination, Integer seats, String selectionCriteria) {
        User user  = userService.getUser(username);
        if(user == null){
            System.out.println("User not found: "+username);
            return null;
        }
        SelectionCriteria criteria = new SelectionCriteria(selectionCriteria);
        SelectionStrategy selectionStrategy = selectionStrategyMap.get(criteria.getName());
        if(selectionStrategy == null) {
            System.out.println("Unsupported Criteria");
            return null;
        }
        if(seats<1 || seats >2){
            System.out.println("Unsupported Number Of Seats");
            return null;
        }
        List<Ride> activeMatchingRides  = activeMatchingRides(origin,destination,seats);
        Ride ride = selectionStrategy.selectRide(activeMatchingRides,criteria);
        if(ride != null){
            Booking booking = new Booking(ride,user);
            String bookingId = String.valueOf(bId.addAndGet(1));
            booking.setId(bookingId);
            bookingMap.put(bookingId,booking);
            ride.getRides().add(booking);
            ride.setSeatsAvailable(ride.getSeatsAvailable()-seats);
            if(ride.getSeatsAvailable() == 0){
                ride.setRideStatus(RideStatus.FILLED);
            }
            return booking;
        }
        return null;
     }

    private List<Ride> activeMatchingRides(String source,String destination,Integer seats){
        Collection<Ride> rides = rideOffers.values();
        List<Ride> activeRides = new ArrayList<>();
        for(Ride ride:rides){
            if(ride.getRideStatus().equals(RideStatus.CREATED) && ride.getSource().equals(source) &&
               ride.getDestination().equals(destination) && ride.getSeatsAvailable()>= seats){
                activeRides.add(ride);
            }
        }
        return activeRides;
    }

    public void printStats(){
        Collection<User> users = userService.getAllUsers();
        for(User user:users){
            System.out.println("User: "+user.getName()+"- ridesTaken:"+user.getRidesTaken()+ "- ridesOffered:"+ user.getRidesOffered());
        }
    }

    public void endRide(String rideId){
        Ride ride = rideMap.get(rideId);
        if(ride == null){
            System.out.println("Ride doesn't exits");
            return;
        }
        ride.setRideStatus(RideStatus.COMPLETED);
        ride.getUser().setRidesOffered(ride.getUser().getRidesOffered()+1);
        List<Booking> bookings = ride.getRides();
        for(Booking booking:bookings){
            booking.setBookingStatus(BookingStatus.COMPLETED);
            booking.getUser().setRidesTaken(booking.getUser().getRidesTaken()+1);
        }
    }
}
