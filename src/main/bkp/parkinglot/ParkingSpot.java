package com.test;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ParkingSpot {
    private final SpotType spotType;
    private String vehicleNumber;
    private String spotId;
    private String ticketNumber;
    private boolean isParkable;
    private boolean isFree;

    public ParkingSpot(SpotType spotType,boolean isParkable) {
        this.spotType = spotType;
        this.isParkable = isParkable;
        this.isFree = true;
    }

    public boolean isFreeForParking(){
        return this.isParkable && this.isFree;
    }
    public void freeSpot(){
        this.setFree(true);
    }
    public void reserveSpot(){
        this.setFree(false);
    }
}
