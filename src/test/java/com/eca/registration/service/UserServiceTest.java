package com.eca.registration.service;

import com.eca.registration.dao.RoleRepository;
import com.eca.registration.dao.SessionRepository;
import com.eca.registration.dao.UserRepository;
import com.eca.registration.mockdata.UserMockData;
import com.eca.registration.model.ERole;
import com.eca.registration.model.Role;
import com.eca.registration.model.User;
import com.eca.registration.model.UserSession;
import com.eca.registration.payload.request.LoginRequest;
import com.eca.registration.payload.request.SignupRequest;
import com.eca.registration.payload.response.MessageResponse;
import com.eca.registration.payload.response.UserInfoResponse;
import com.eca.registration.security.jwt.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@SpringBootTest
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private RoleRepository roleRepository;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private SessionRepository sessionRepository;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegisterUser_Success() {
        SignupRequest signupRequest = UserMockData.mockSignupRequest();
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        Role role = new Role();
        role.setRole(ERole.OWNER);
        when(roleRepository.findByRole(any(ERole.class))).thenReturn(Optional.of(role));
        when(userRepository.save(Mockito.any(User.class))).thenReturn(UserMockData.mockUserResponse());
        ResponseEntity<MessageResponse> response = userService.registerUser(signupRequest);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("User registered successfully!", response.getBody().getMessage());
    }

    @Test
    void testRegisterUser_EmailExist() {
        SignupRequest signupRequest = UserMockData.mockSignupRequest();
        when(userRepository.existsByEmail(anyString())).thenReturn(true);
        ResponseEntity<MessageResponse> response = userService.registerUser(signupRequest);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Error: Email is already in use!", response.getBody().getMessage());
    }


    @Test
    void testRegisterUser_UserExist() {
        SignupRequest signupRequest = UserMockData.mockSignupRequest();
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByUsername(anyString())).thenReturn(true);
        ResponseEntity<MessageResponse> response = userService.registerUser(signupRequest);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Error: Username is already in use!", response.getBody().getMessage());
    }


    @Test
    void testRegisterUser_EmptyUserRole() {
        SignupRequest signupRequest = UserMockData.mockSignupEmptyRoleRequest();
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        ResponseEntity<MessageResponse> response = userService.registerUser(signupRequest);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Error: Enter the user role!", response.getBody().getMessage());
    }


    @Test
    void testRegisterUser_Failure() {
        SignupRequest signupRequest = UserMockData.mockSignupRequest();
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        Role role = new Role();
        role.setRole(ERole.OWNER);
        when(roleRepository.findByRole(any(ERole.class))).thenReturn(Optional.of(role));
        when(userRepository.save(Mockito.any(User.class))).thenReturn(null);
        ResponseEntity<MessageResponse> response = userService.registerUser(signupRequest);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Error: User can't be registered", response.getBody().getMessage());
    }


    @Test
    void testLoginUser_Success() {
        LoginRequest loginRequest = UserMockData.mockLoginRequest();

        User user = UserMockData.mockUserResponse();

        when(userRepository.findByUsername(anyString())).thenReturn(Optional.ofNullable(user));

        String token = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJoYXJtZWV0IiwiaWF0IjoxNjg3Mjg3OTA1LCJleHAiOjE2ODcyODk3MDV9.kIeiY-UMQVNvdKSDB6T4vW28Tug_8OE7dmzbPhhMQRw";
        when(jwtService.generateToken(user.getUsername())).thenReturn(token);

        UserSession userSession = UserMockData.mockUserSession();
        when(jwtService.extractExpiration(anyString())).thenReturn(userSession.getTokenExpirationTime());

        when(sessionRepository.save(Mockito.any(UserSession.class))).thenReturn(userSession);

        ResponseEntity<MessageResponse> response = userService.loginUser(loginRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Logged in", response.getBody().getMessage());
    }


    @Test
    void testLoginUserNull_BadReq() {
        LoginRequest loginRequest = UserMockData.mockLoginRequest();
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());
        ResponseEntity<MessageResponse> response = userService.loginUser(loginRequest);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Error: User not found!", response.getBody().getMessage());
    }

    @Test
    void testLoginUserEmptyToken_BadReq() {
        LoginRequest loginRequest = UserMockData.mockLoginRequest();
        User user = UserMockData.mockUserResponse();
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.ofNullable(user));
        when(jwtService.generateToken(user.getUsername())).thenReturn(null);
        ResponseEntity<MessageResponse> response = userService.loginUser(loginRequest);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Error Occurred while logging in!", response.getBody().getMessage());
    }


    @Test
    void testLogoutUser_Success() {
        Long userId = 123L;
        String username = "testuser";
        UserSession userSession = UserMockData.mockUserSession();
        when(sessionRepository.findByUserId(anyLong())).thenReturn(Optional.of(userSession));
        ResponseEntity<MessageResponse> response = userService.logoutUser(userId, username);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("You've been signed out!", response.getBody().getMessage());
    }

    @Test
    void testLogoutUser_UserNotLoggedIn(){
        Long userId = 123L;
        String username = "testuser";
        when(sessionRepository.findByUserId(userId)).thenReturn(Optional.empty());
        ResponseEntity<MessageResponse> response = userService.logoutUser(userId, username);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Error: Either user not found or User not logged-in!", response.getBody().getMessage());

    }

    @Test
    void testFetchUserByFlatNoAndTowerNo_UserFound() {
        int flatNo = 109;
        int towerNo = 9;

        User user = UserMockData.mockUserResponse();
        when(userRepository.findByFlatNoAndTowerNo(anyInt(), anyInt())).thenReturn(Optional.of(user));
        ResponseEntity<MessageResponse> response = userService.fetchUserByFlatNoAndTowerNo(flatNo, towerNo);
        UserInfoResponse userResult = (UserInfoResponse) response.getBody().getBody();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("TestUser",userResult.getUsername());
        assertEquals(flatNo, userResult.getFlatNo());
    }

    @Test
    void testFetchUserByFlatNoAndTowerNo_UserNotFound() {
        int flatNo = 109;
        int towerNo = 9;

        when(userRepository.findByFlatNoAndTowerNo(flatNo, towerNo)).thenReturn(Optional.empty());
        ResponseEntity<MessageResponse> response = userService.fetchUserByFlatNoAndTowerNo(flatNo, towerNo);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Error: User not found!", response.getBody().getMessage());

    }

    @Test
    void testFetchUserByUsername_UserFound() {
        User user = UserMockData.mockUserResponse();
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        ResponseEntity<MessageResponse> response = userService.fetchUserByUsername("TestUser");
        UserInfoResponse userResult = (UserInfoResponse) response.getBody().getBody();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("TestUser",userResult.getUsername());
    }

    @Test
    void testFetchUserByUsername_UserNotFound() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());
        ResponseEntity<MessageResponse> response = userService.fetchUserByUsername("TestUser");
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Error: User not found!", response.getBody().getMessage());

    }





}
