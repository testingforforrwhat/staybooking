package com.test.staybooking.location;


public class GeocodingException extends RuntimeException {


   public GeocodingException() {
       super("Failed to look up address");
   }
}
