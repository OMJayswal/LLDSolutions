package com.test;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParkingLot {
    private ParkingSpot[][][] parkingSpots;
    private Map<String,ParkingSpot> ticketSpotMap = new HashMap<>();
    private Map<String, List<ParkingSpot>> vehicleSpotMap = new HashMap<>();

    private  int floors;
    private  int rows;
    private  int cols;
    private Helper helper;

    public void init(Helper helper,String [][][] parking){
        this.helper = helper;
        this.floors = parking.length;
        this.rows = parking[0].length;
        this.cols = parking[0][0].length;
        this.parkingSpots = new ParkingSpot[floors][rows][cols];
        for(int i=0;i<floors;i++){
            for(int j=0;j<rows;j++){
                for(int k=0;k<cols;k++){
                    String s = parking[i][j][k];
                    SpotType type = SpotType.fromValue(Integer.parseInt(s.split("-")[0]));
                    boolean isParkable = s.split("-")[1].equals("1");
                    this.parkingSpots[i][j][k] = new ParkingSpot(type,isParkable);
                }
            }
        }
    }

    public ParkingResult park(SpotType vehicleType,String vehicleNumber,String ticketId){
        ParkingResult parkingResult = new ParkingResult("404","",vehicleNumber,ticketId);
        List<ParkingSpot> parkingSpotList = vehicleSpotMap.get(vehicleNumber);
        ParkingSpot parkingSpot = parkingSpotList == null ? null : parkingSpotList.get(parkingSpotList.size()-1);
        if(parkingSpot!=null  &&  !parkingSpot.isFreeForParking()){
            helper.println("VehicleNumber: "+vehicleNumber+" is already parked");
            return parkingResult;
        }
        if(ticketSpotMap.containsKey(ticketId)){
            helper.println("Ticket: "+ticketId+" is already issued");
            return parkingResult;
        }
        for(int i=0;i<floors;i++) {
            for (int j = 0; j < rows; j++) {
                for (int k = 0; k < cols; k++) {
                    ParkingSpot spot = parkingSpots[i][j][k];
                    if(spot.getSpotType().equals(vehicleType) && spot.isFreeForParking()){
                      String spotId = i+"-"+j+"-"+k;
                      spot.reserveSpot();
                      spot.setSpotId(spotId);
                      spot.setTicketNumber(ticketId);
                      spot.setVehicleNumber(vehicleNumber);
                      ticketSpotMap.put(ticketId,spot);
                      vehicleSpotMap.computeIfAbsent(vehicleNumber,v -> new ArrayList<>()).add(spot);
                      parkingResult.setStatus("201");
                      parkingResult.setSpotId(spotId);
                      return parkingResult;
                   }
                }
            }
        }
        return parkingResult;
    }


    public void removeVehicle(String spotId, String vehicleNumber, String ticketId){
       if(spotId!=null && !spotId.isEmpty()){
          String[] index = spotId.split("-");
          int f =  Integer.parseInt(index[0]);
          int r =  Integer.parseInt(index[1]);
          int c =  Integer.parseInt(index[2]);
          parkingSpots[f][r][c].freeSpot();
          return;
       }
       if(vehicleNumber!=null && !vehicleNumber.isEmpty()){
           if(!vehicleSpotMap.containsKey(vehicleNumber)){
               helper.println("Vehicle: "+vehicleNumber+ " not found");
               return;
           }
           List<ParkingSpot> parkingSpot = vehicleSpotMap.get(vehicleNumber);
           parkingSpot.get(parkingSpot.size()-1).freeSpot();
           return;
       }
        if(ticketId!=null && !ticketId.isEmpty()) {
            if (!ticketSpotMap.containsKey(ticketId)) {
                helper.println("Ticket: " + ticketId + " not found");
                return;
            }
            ParkingSpot parkingSpot = ticketSpotMap.get(ticketId);
            parkingSpot.freeSpot();
        }
    }

    public ParkingResult searchVehicle(String spotId,String vehicleNumber,String ticketId){
        if(spotId!=null && !spotId.isEmpty()){
            String[] index = spotId.split("-");
            int f =  Integer.parseInt(index[0]);
            int r =  Integer.parseInt(index[1]);
            int c =  Integer.parseInt(index[2]);
            if(f>=floors || r>=rows || c>=cols || f<0 || c<0 || r<0) {
                helper.println("Indexes are out of boind for the spotId");
                return new ParkingResult("404",spotId,null,null);
            }
            ParkingSpot parkingSpot = parkingSpots[f][r][c];
            return new ParkingResult("201",spotId,
                    parkingSpot.getVehicleNumber(),parkingSpot.getTicketNumber());
        }
        else if(vehicleNumber!=null && !vehicleNumber.isEmpty()){
            List<ParkingSpot> parkingSpot =  vehicleSpotMap.get(vehicleNumber);
            if(parkingSpot == null || parkingSpot.isEmpty()){
                helper.println("Vehicle not found");
                return new ParkingResult("404",null,vehicleNumber,null);
            }
            ParkingSpot recentSpot = parkingSpot.get(parkingSpot.size()-1);
            return new ParkingResult("201",spotId,
                    recentSpot.getVehicleNumber(),recentSpot.getTicketNumber());
        }else if(ticketId != null && !ticketId.isEmpty()){
           ParkingSpot parkingSpot = ticketSpotMap.get(ticketId);
           if(parkingSpot == null){
               helper.println("Parking Ticket not found");
               return new ParkingResult("404",null,null,ticketId);
           }
            return new ParkingResult("201",parkingSpot.getSpotId(),
                    parkingSpot.getVehicleNumber(),parkingSpot.getTicketNumber());
        }
        return  new ParkingResult("404",null,null,null);
    }

    public int getFreeSpotsCount(int floor,int vehicleType){
        SpotType spotType = SpotType.fromValue(vehicleType);
        ParkingSpot [][] spots = parkingSpots[floor];
        int count =0;
        for(int i=0;i<rows;i++){
            for(int j=0;j<cols;j++){
                ParkingSpot spot = spots[i][j];
                if(spot.getSpotType().equals(spotType) && spot.isFreeForParking()){
                    count++;
                }
            }
        }
        return count;
    }

}
