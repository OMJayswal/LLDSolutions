package com.test;

public enum SpotType {
    TWOWHELLER(2),FOURWHEELER(4);
    private int value;
    SpotType(int val){
        this.value = val;
    }
    public int getValue(SpotType spotType){
        return this.value;
    }

    public static SpotType fromValue(int val) {
        for (SpotType spotType : SpotType.values()) {
            if (spotType.value == val) {
                return spotType;
            }
        }
        throw new IllegalArgumentException("No SpotType with value " + val);
    }
}
