package com.test;

import lombok.Data;

@Data
public class ParkingResult {
    private  String status;
    private  String spotId;
    private  String vehicleNumber;
    private  String ticketId;

    public ParkingResult(String status, String spotId, String vehicleNumber, String ticketId) {
        this.status = status;
        this.spotId = spotId;
        this.vehicleNumber = vehicleNumber;
        this.ticketId = ticketId;
    }
}
