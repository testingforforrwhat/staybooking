package com.test.staybooking.model;


import java.time.LocalDate;


public record BookingRequest(
       long listingId,
       LocalDate checkInDate,
       LocalDate checkOutDate
) {
}
