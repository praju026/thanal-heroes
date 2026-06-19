package com.thanal.thanal_heroes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thanal.thanal_heroes.controller.AuthController;
import com.thanal.thanal_heroes.dto.AuthRequest;
import com.thanal.thanal_heroes.dto.AuthResponse;
import com.thanal.thanal_heroes.dto.RegisterRequest;
import com.thanal.thanal_heroes.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class SecurityAndAuthTests {

    private MockMvc mockMvc;

    @Autowired
    private AuthController authController;

    @Autowired
    private UserRepository userRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setup() {
        userRepository.deleteAll();
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    }

    @Test
    public void testRegistrationAndLoginFlow() throws Exception {
        // 1. Register User
        RegisterRequest registerReq = new RegisterRequest("john_doe", "password123", "ADMIN");
        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerReq)))
                .andExpect(status().isOk());

        // 2. Login User to get Token
        AuthRequest authReq = new AuthRequest("john_doe", "password123");
        MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authReq)))
                .andExpect(status().isOk())
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        Map<?, ?> responseMap = objectMapper.readValue(responseContent, Map.class);
        String token = (String) responseMap.get("token");

        Assertions.assertNotNull(token);
    }
}
