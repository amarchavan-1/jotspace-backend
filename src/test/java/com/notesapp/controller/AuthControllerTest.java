package com.notesapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.notesapp.dto.LoginRequest;
import com.notesapp.dto.LoginResponse;
import com.notesapp.dto.RegisterRequest;
import com.notesapp.security.JwtAuthEntryPoint;
import com.notesapp.security.JwtAuthenticationFilter;
import com.notesapp.service.AuthService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AuthController.class)
@AutoConfigureMockMvc(addFilters = false) // Disable security filters to unit-test the controller layer cleanly
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    // Mocking security beans that might otherwise trigger auto-wiring issues
    @MockBean
    private JwtAuthEntryPoint jwtAuthEntryPoint;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testRegisterUser_Success() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setName("Test User");
        registerRequest.setEmail("test@test.com");
        registerRequest.setPassword("secure123");

        // Mock the service layer action
        Mockito.when(authService.registerUser(any(RegisterRequest.class)))
                .thenReturn("User registered successfully");

        // Perform the request and assert status & body
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().string("User registered successfully"));
    }

    @Test
    public void testLoginUser_Success() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("test@test.com");
        loginRequest.setPassword("secure123");

        LoginResponse loginResponse = new LoginResponse("mock_jwt_token", "Bearer");

        // Mock the service layer auth check
        Mockito.when(authService.authenticateUser(any(LoginRequest.class)))
                .thenReturn(loginResponse);

        // Perform the request and assert status, cookie generation, and body
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(cookie().exists("jwt"))
                .andExpect(jsonPath("$.message").value("Logged in successfully"));
    }
}
