package com.eca.registration.controller;

import com.eca.registration.payload.request.LoginRequest;
import com.eca.registration.payload.request.SignupRequest;
import com.eca.registration.payload.response.MessageResponse;
import com.eca.registration.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/eca/v1/user")
@Tag(name = "UserController", description = "The Authentication API. Contains operations like login, logout, sign-up etc.")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signupRequest) {
        try{
            return userService.registerUser(signupRequest);
           }
        catch (Exception e){
            throw new RuntimeException(e.getCause());
        }
    }

    @PostMapping("/signin")
    public ResponseEntity<?> loginUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            Authentication authenticate = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
            if (authenticate.isAuthenticated()) {
                return userService.loginUser(loginRequest);
            }else {
                return ResponseEntity.badRequest().body(MessageResponse.builder().message("Error: Please enter correct username/password!").build());
            }
        } catch (Exception e){
            throw new RuntimeException(e.getCause());
        }
    }

    @PostMapping("/signout/{id}/{username}")
    @SecurityRequirement(name = "Authorization")
    public ResponseEntity<?> logoutUser(@NonNull @PathVariable Long id, @NonNull @PathVariable String username) {
        try {
            return  userService.logoutUser(id, username);
        }catch (Exception e){
            throw new RuntimeException(e.getCause());
        }

    }

    @GetMapping()
    public ResponseEntity<?> fetchUserByFlatNoAndTowerNo(@NonNull @RequestParam int flatNo, @NonNull @RequestParam int towerNo){
        try{
            return userService.fetchUserByFlatNoAndTowerNo(flatNo,towerNo);
        }catch (Exception e){
            throw new RuntimeException(e.getCause());
        }
    }

    @GetMapping("/{username}")
    public ResponseEntity<?> fetchUserByUsername(@PathVariable String username){
        try{
            return userService.fetchUserByUsername(username);
        }catch (Exception e){
            throw new RuntimeException(e.getCause());
        }
    }


}
