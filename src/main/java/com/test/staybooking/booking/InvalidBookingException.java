package com.test.staybooking.booking;


public class InvalidBookingException extends RuntimeException{


   public InvalidBookingException(String message) {
       super(message);
   }
}
