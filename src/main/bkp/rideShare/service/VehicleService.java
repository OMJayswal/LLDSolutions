package com.test.service;

import com.test.model.User;
import com.test.model.Vehicle;

import java.util.HashMap;
import java.util.Map;

public class VehicleService {
    private UserService userService;
    public VehicleService(UserService userService){
        this.userService = userService;
    }
    private final Map<String, Vehicle> vehicleMap = new HashMap<>();
    public Vehicle addVehicle(String username,String vehicleNumber,String modelNumber) throws  Exception{
        if(vehicleMap.containsKey(vehicleNumber)){
            throw new Exception("Vehicle already exists");
        }
        User user = userService.getUser(username);
        if(user == null){
            throw new Exception("User doesn't exist");
        }
        Vehicle vehicle = new Vehicle(vehicleNumber,modelNumber,user);
        vehicleMap.put(vehicleNumber,vehicle);
        return vehicle;
    }
    public Vehicle getVehicle(String vehicleNumber){
        return vehicleMap.getOrDefault(vehicleNumber,null);
    }
}
