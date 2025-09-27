package com.test;

/*
 UserService
 - Map<Username,User> userMap;
 + addUser()
 + getUser()

 VehicleService
 - Map<VehicleNumber,Vehicle> vehicleMap;
 + addVehicle()
 + getVehicle()

 RideShareService
 - Map<Map.Entry<String,String>,Ride> rideOffers;
 - Map<String,SelectionStrategy> strategiesMap;
 - Map<Username,UserStats> userStats;
 + offer_ride()
 + select_ride()
 + end_ride()
 + print_ride_stats()

*/

import com.test.model.Booking;
import com.test.model.Ride;
import com.test.service.RideShareService;
import com.test.service.UserService;
import com.test.service.VehicleService;

public class Main {
    public static void main(String [] args) throws Exception {
        UserService userService = new UserService();
        VehicleService vehicleService = new VehicleService(userService);
        RideShareService rideShareService = new RideShareService(userService,vehicleService);

        userService.addUser("Rohan","M",36);
        vehicleService.addVehicle("Rohan","KA-01-12345","Swift");
        userService.addUser("Shashank","M",29);
        vehicleService.addVehicle("Shashank","TS-05-62395","Baleno");
        userService.addUser("Nandhini","F",29);
        userService.addUser("Shipra","F",27);
        vehicleService.addVehicle("Shipra","KA-05-41491","Polo");
        vehicleService.addVehicle("Shipra","KA-12-12332","Activa");
        userService.addUser("Gaurav","M",29);
        userService.addUser("Rahul","M",35);
        vehicleService.addVehicle("Rahul","KA-05-1234","XUV");

        Ride ride1 = rideShareService.offerRide("Rohan","Hyderabad",1,"Swift","KA-01-12345","Bangalore");
        Ride ride2 = rideShareService.offerRide("Shipra","Bangalore",1,"Activa","KA-12-12332","Mysore");
        Ride ride3 = rideShareService.offerRide("Shipra","Bangalore",2,"Polo","KA-05-41491","Mysore");
        Ride ride4 = rideShareService.offerRide("Shashank","Hyderabad",2,"Baleno","TS-05-62395","Bangalore");
        Ride ride5 = rideShareService.offerRide("Rahul","Hyderabad",5,"XUV","KA-05-1234","Bangalore");
        Ride ride6 = rideShareService.offerRide("Rohan","Bangalore",1,"Swift","KA-01-12345","Pune");

        rideShareService.selectRide("Nandhini","Bangalore","Mysore",1,"Most Vacant");
        rideShareService.selectRide("Gaurav","Bangalore","Mysore",1,"Preferred Vehicle=Activa");
        rideShareService.selectRide("Shashank","Mumbai","Bangalore",1,"Most Vacant");
        rideShareService.selectRide("Rohan","Hyderabad","Bangalore",1,"Preferred Vehicle=Baleno");
        rideShareService.selectRide("Shashank","Hyderabad","Bangalore",1,"Preferred Vehicle=Polo");

        rideShareService.endRide(ride1.getId());
        rideShareService.endRide(ride2.getId());
        rideShareService.endRide(ride3.getId());
        rideShareService.endRide(ride4.getId());
        rideShareService.printStats();
    }
}
