package com.test.staybooking.authentication;


import com.test.staybooking.model.LoginRequest;
import com.test.staybooking.model.LoginResponse;
import com.test.staybooking.model.RegisterRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/auth")
public class AuthenticationController {


   private final AuthenticationService authenticationService;


   public AuthenticationController(AuthenticationService authenticationService) {
       this.authenticationService = authenticationService;
   }


   @PostMapping("/register")
   @ResponseStatus(HttpStatus.CREATED)
   public void register(@RequestBody RegisterRequest body) {
       authenticationService.register(body.username(), body.password(), body.role());
   }


   @PostMapping("/login")
   public LoginResponse login(@RequestBody LoginRequest body) {
       String token = authenticationService.login(body.username(), body.password());
       return new LoginResponse(token);
   }
}