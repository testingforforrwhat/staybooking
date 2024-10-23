package com.test.staybooking.booking;


import com.test.staybooking.model.BookingDto;
import com.test.staybooking.model.BookingRequest;
import com.test.staybooking.model.UserEntity;
import com.test.staybooking.model.UserRole;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


import java.util.List;


@RestController
@RequestMapping("/bookings")
public class BookingController {


   private final BookingService bookingService;


//   private final UserEntity user = new UserEntity(4L, "lively_wanderlust", "1z3dUW", UserRole.ROLE_GUEST);


   public BookingController(BookingService bookingService) {
       this.bookingService = bookingService;
   }


   @GetMapping
   public List<BookingDto> getGuestBookings(@AuthenticationPrincipal UserEntity user) {
       return bookingService.findBookingsByGuestId(user.getId());
   }


   @PostMapping
   @ResponseStatus(HttpStatus.CREATED)
   public void createBooking(@AuthenticationPrincipal UserEntity user, @RequestBody BookingRequest body) {
       bookingService.createBooking(user.getId(), body.listingId(), body.checkInDate(), body.checkOutDate());
   }


   @DeleteMapping("/{bookingId}")
   @ResponseStatus(HttpStatus.NO_CONTENT)
   public void deleteBooking(@AuthenticationPrincipal UserEntity user, @PathVariable long bookingId) {
       bookingService.deleteBooking(user.getId(), bookingId);
   }
}
