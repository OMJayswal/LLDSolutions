package com.test.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class Vehicle {
    private final String vehicleNumber;
    private final String vehicleModel;
    private final User owner;
}
