package com.eca.registration.serviceimpl;

import com.eca.registration.commons.CommonUtils;
import com.eca.registration.dao.RoleRepository;
import com.eca.registration.dao.SessionRepository;
import com.eca.registration.dao.UserRepository;
import com.eca.registration.model.Role;
import com.eca.registration.model.User;
import com.eca.registration.model.UserSession;
import com.eca.registration.payload.request.LoginRequest;
import com.eca.registration.payload.request.SignupRequest;
import com.eca.registration.payload.response.LoginResponse;
import com.eca.registration.payload.response.MessageResponse;
import com.eca.registration.payload.response.UserInfoResponse;
import com.eca.registration.security.jwt.JwtService;
import com.eca.registration.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;
    @Autowired
    JwtService jwtService;
    @Autowired
    private PasswordEncoder encoder;
    //
    @Autowired
    private SessionRepository sessionRepository;

    @Override
    public ResponseEntity<MessageResponse> registerUser(SignupRequest signUpRequest) {
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity.badRequest().body(MessageResponse.builder().message("Error: Email is already in use!").build());
        }
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity.badRequest().body(MessageResponse.builder().message("Error: Username is already in use!").build());
        }
        if (null == signUpRequest.getRole() ||CommonUtils.isEmptyString(signUpRequest.getRole().toString())) {
            return ResponseEntity.badRequest().body(MessageResponse.builder().message("Error: Enter the user role!").build());
        }

        // Create new user's account
        Role role = roleRepository.findByRole(signUpRequest.getRole())
                .orElse(null);
        if (null == role) {
            return ResponseEntity.badRequest().body(MessageResponse.builder().message("Error: Enter the correct user role!").build());
        }
        User user =
                User.builder()
                        .username(signUpRequest.getUsername())
                        .password(encoder.encode(signUpRequest.getPassword()))
                        .roles(role.getRole())
                        .flatNo(signUpRequest.getFlatNo())
                        .towerNo(signUpRequest.getTowerNo())
                        .phoneNo(signUpRequest.getPhoneNo())
                        .email(signUpRequest.getEmail()).build();

        User registeredUser = userRepository.save(user);
        if (null != registeredUser && registeredUser.getUsername().equals(signUpRequest.getUsername()))
            return ResponseEntity.ok(MessageResponse.builder().message("User registered successfully!").build());
        else
            return ResponseEntity.badRequest().body(MessageResponse.builder().message("Error: User can't be registered").build());

    }

    @Override
    @Transactional
    public ResponseEntity<MessageResponse> loginUser(LoginRequest loginRequest) {
        User user = userRepository.findByUsername(loginRequest.getUsername()).orElse(null);
        if (null == user ) {
            return ResponseEntity.badRequest().body(MessageResponse.builder().message("Error: User not found!").build());
        }
        String token = jwtService.generateToken(user.getUsername());
        if (null == token) {
            return ResponseEntity.badRequest().body(MessageResponse.builder().message("Error Occurred while logging in!").build());
        }
        Date date = jwtService.extractExpiration(token);
        UserSession userSession = UserSession.builder()
                .userId(user.getId())
                .token(token)
                .tokenExpirationTime(date)
                .lastLoginTime(new Date(System.currentTimeMillis())).build();

        sessionRepository.save(userSession);
        return ResponseEntity.ok(MessageResponse.builder().message("Logged in").body(setLoginResponse(token, user)).build());
    }

    private LoginResponse setLoginResponse(String token, User user) {
        return LoginResponse.builder()
                .token(token).user(UserInfoResponse.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRoles().toString())
                .userId(user.getId())
                .build()).build();
    }

    @Override
    @Transactional
    public ResponseEntity<MessageResponse> logoutUser(Long id, String username) {
        UserSession userSession = sessionRepository.findByUserId(id).orElse(null);
        if (null == userSession || CommonUtils.isEmptyString(userSession.getToken())) {
            return ResponseEntity.badRequest().body(MessageResponse.builder().message("Error: Either user not found or User not logged-in!").build());
        }
        userSession.setToken("");
        userSession.setTokenExpirationTime(null);
        sessionRepository.save(userSession);
        return ResponseEntity.ok(MessageResponse.builder().message("You've been signed out!").build());
    }

    @Override
    public ResponseEntity<MessageResponse> fetchUserByFlatNoAndTowerNo(int flatNo, int towerNo) {
        User user = userRepository.findByFlatNoAndTowerNo(flatNo, towerNo).orElse(null);
        if (null == user ) {
            return ResponseEntity.badRequest().body(MessageResponse.builder().message("Error: User not found!").build());
        }
        return ResponseEntity.ok(MessageResponse.builder().body(setUserDetails(user)).build());
    }

    @Override
    public ResponseEntity<MessageResponse> fetchUserByUsername(String username) {
        User user = userRepository.findByUsername(username).orElse(null);
        if (null == user ) {
            return ResponseEntity.badRequest().body(MessageResponse.builder().message("Error: User not found!").build());
        }
        return ResponseEntity.ok(MessageResponse.builder().body(setUserDetails(user)).build());
    }

    private UserInfoResponse setUserDetails(User user) {
        return UserInfoResponse.builder()
                .towerNo(user.getTowerNo())
                .flatNo(user.getFlatNo())
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .phoneNo(user.getPhoneNo())
                .build();
    }
}
