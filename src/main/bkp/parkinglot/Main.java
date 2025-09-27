package com.test;

public class Main {
    public static void main(String []args) {

        String[][][] parkingSpots = {{
                {"4-1", "4-1", "2-1", "2-0"},
                {"2-1", "4-1", "2-1", "2-1"},
                {"4-0", "2-1", "4-0", "2-1"},
                {"4-1", "4-1", "4-1", "2-1"}
        }};
        ParkingLot parkingLot = new ParkingLot();
        Helper helper = new Helper();
        parkingLot.init(helper,parkingSpots);
        System.out.println(parkingLot.getFreeSpotsCount(0,4));
        System.out.println(parkingLot.park(SpotType.FOURWHEELER,"bh234","tkt4534"));
        System.out.println(parkingLot.searchVehicle(null,null,"tkt4534"));
        System.out.println(parkingLot.getFreeSpotsCount(0,4));
        parkingLot.removeVehicle(null,null,"tkt4534");
        System.out.println(parkingLot.getFreeSpotsCount(0,4));
    }
}
