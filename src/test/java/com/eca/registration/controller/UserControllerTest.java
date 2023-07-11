package com.eca.registration.controller;

import com.eca.registration.service.UserService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static com.eca.registration.mockdata.UserMockData.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class UserControllerTest {

    @MockBean
    Authentication authentication;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private UserService userService;
    @MockBean
    private AuthenticationManager authenticationManager;

    @Autowired
    private WebApplicationContext webApplicationContext;
    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void registerUser_Test() throws Exception {
        when(userService.registerUser(Mockito.any())).thenReturn(mockSignUpMessageResponse());
        MvcResult mvcResult = mockMvc.perform(post("/eca/v1/user/signup")
                        .content(asJsonString(mockSignupRequest()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode resultJson = objectMapper.readTree(mvcResult.getResponse().getContentAsString());
        assertNotNull(mvcResult);
        assertEquals(objectMapper.writeValueAsString(resultJson.get("message")), "\"User registered successfully!\"");
        verify(userService, Mockito.times(1)).registerUser(Mockito.any());
        verifyNoMoreInteractions(userService);
    }

    @Test
    void registerUser_Exception() throws Exception {
        when(userService.registerUser(Mockito.any())).thenThrow(new RuntimeException("Something went wrong"));

        // Act and Assert
        Exception exception = assertThrows(Exception.class,()->{mockMvc.perform(post("/eca/v1/user/signup")
                        .content(asJsonString(mockSignupRequest()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andReturn();});

        assertNotNull(exception);
        verify(userService, times(1)).registerUser(Mockito.any());
        verifyNoMoreInteractions(userService);
    }


    @Test
    public void loginUser_Test() throws Exception {
        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(userService.loginUser(Mockito.any())).thenReturn(mockLoginMessageResponse());
        MvcResult mvcResult = mockMvc.perform(post("/eca/v1/user/signin")
                        .content(asJsonString(mockLoginRequest()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode resultJson = objectMapper.readTree(mvcResult.getResponse().getContentAsString());
        assertNotNull(mvcResult);
        verify(userService, Mockito.times(1)).loginUser(Mockito.any());
        assertEquals(objectMapper.writeValueAsString(resultJson.get("body").get("user").get("username")), "\"TestUser\"");
        verifyNoMoreInteractions(userService);
    }

    @Test
    public void logoutUser_Test() throws Exception {
        Long userId = 123L;
        String username = "testuser";
        when(userService.logoutUser(anyLong(), anyString()))
                .thenReturn(mockLogoutMessageResponse());

        MvcResult mvcResult = mockMvc.perform(
                        post("/eca/v1/user/signout/{id}/{username}", userId, username)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode resultJson = objectMapper.readTree(mvcResult.getResponse().getContentAsString());
        assertNotNull(mvcResult);
        assertEquals(objectMapper.writeValueAsString(resultJson.get("message")), "\"You've been signed out!\"");
        verify(userService, Mockito.times(1)).logoutUser(anyLong(),anyString());
        verifyNoMoreInteractions(userService);
    }

    @Test
    public void logoutUser_Test_ExceptionThrown() throws Exception {
        Long userId = 123L;
        String username = "testuser";

        when(userService.logoutUser(userId, username))
                .thenThrow(new RuntimeException("Something went wrong."));

        Exception exception = assertThrows(Exception.class, () -> {
            mockMvc.perform(
                            post("/eca/v1/user/signout/{id}/{username}", userId, username)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isInternalServerError())
                    .andReturn();
        });
        assertNotNull(exception);
        verify(userService, times(1)).logoutUser(Mockito.anyLong(),Mockito.anyString());
        verifyNoMoreInteractions(userService);
    }


    @Test
    void testFetchUserByFlatNoAndTowerNo_Success() throws Exception {

        when(userService.fetchUserByFlatNoAndTowerNo(101, 9)).thenReturn(mockUserMessageResponse());

        MvcResult mvcResult = mockMvc.perform(
                        get("/eca/v1/user")
                                .param("flatNo", String.valueOf(101))
                                .param("towerNo", String.valueOf(9)))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode resultJson = objectMapper.readTree(mvcResult.getResponse().getContentAsString());
        assertNotNull(mvcResult);
        assertEquals(objectMapper.writeValueAsString(resultJson.get("body").get("email")), "\"test@gmail.com\"");
        verify(userService, Mockito.times(1)).fetchUserByFlatNoAndTowerNo(anyInt(),anyInt());
        verifyNoMoreInteractions(userService);
    }

    @Test
    public void testFetchUserByFlatNoAndTowerNo_ExceptionThrown() throws Exception {

        when(userService.fetchUserByFlatNoAndTowerNo(101, 9))
                .thenThrow(new RuntimeException("USER nOT FOUND!"));

        Exception exception = assertThrows(Exception.class, () -> {
            mockMvc.perform(
                            get("/eca/v1/user")
                                    .param("flatNo", String.valueOf(101))
                                    .param("towerNo", String.valueOf(9)))
                    .andExpect(status().isInternalServerError())
                    .andReturn();
        });
        assertNotNull(exception);
        verify(userService, times(1)).fetchUserByFlatNoAndTowerNo(Mockito.anyInt(),Mockito.anyInt());
        verifyNoMoreInteractions(userService);
    }

    @Test
    void testFetchUserByUsername_Success() throws Exception {

        when(userService.fetchUserByUsername("test")).thenReturn(mockUserMessageResponse());

        MvcResult mvcResult = mockMvc.perform(
                        get("/eca/v1/user/{username}", "test"))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode resultJson = objectMapper.readTree(mvcResult.getResponse().getContentAsString());
        assertNotNull(mvcResult);
        assertEquals(objectMapper.writeValueAsString(resultJson.get("body").get("email")), "\"test@gmail.com\"");
        verify(userService, Mockito.times(1)).fetchUserByUsername(anyString());
        verifyNoMoreInteractions(userService);
    }

    @Test
    public void testFetchUserByUsername_ExceptionThrown() throws Exception {

        when(userService.fetchUserByUsername("test"))
                .thenThrow(new RuntimeException("USER nOT FOUND!"));

        Exception exception = assertThrows(Exception.class, () -> {
            mockMvc.perform(
                            get("/eca/v1/user/{username}", "test"))
                    .andExpect(status().isInternalServerError())
                    .andReturn();
        });
        assertNotNull(exception);
        verify(userService, times(1)).fetchUserByUsername(anyString());
        verifyNoMoreInteractions(userService);
    }


}
