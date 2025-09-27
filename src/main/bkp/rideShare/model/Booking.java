package com.test.model;

import com.test.dto.BookingStatus;
import com.test.dto.RideStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Booking {
   private final Ride ride;
   private final User user;
   private String id;
   private BookingStatus bookingStatus;
    public Booking(Ride ride,User user) {
      this.ride = ride;
      this.user = user;
      this.bookingStatus = BookingStatus.CREATED;
    }
}
