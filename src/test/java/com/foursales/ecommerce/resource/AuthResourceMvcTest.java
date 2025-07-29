package com.foursales.ecommerce.resource;

import com.foursales.ecommerce.enums.Role;
import com.foursales.ecommerce.resource.request.RefreshTokenRequest;
import com.foursales.ecommerce.resource.request.SignInRequest;
import com.foursales.ecommerce.resource.request.SignOutRequest;
import com.foursales.ecommerce.resource.request.SignUpRequest;
import com.foursales.ecommerce.resource.response.AuthResponse;
import com.foursales.ecommerce.resource.response.UserResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;

import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthResource.class)
class AuthResourceMvcTest extends AbstractResourceMvcTest {

    private final UUID userId = UUID.randomUUID();
    private final String email = "test@email.com";
    private final String password = "validPassword";
    private final String refreshToken = "refresh-token";
    private final String accessToken = "access-token";
    private final String tokenType = "Bearer";
    private final long expiresIn = 300L;
    private final Role role = Role.ADMIN;

    @Nested
    class SignUp {

        @Test
        @DisplayName("Should return 201 and user response on successful signup")
        void shouldReturn201OnSuccess() throws Exception {
            SignUpRequest request = new SignUpRequest(email, password);

            UserResponse response = new UserResponse(userId, email, role);
            String expectedJson = objectMapper.writeValueAsString(response);
            when(authService.signUp(any(SignUpRequest.class))).thenReturn(response);

            mockMvc.perform(post("/v1/auth/signup")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(content().json(expectedJson));

            verify(authService).signUp(any(SignUpRequest.class));
        }

        @Test
        @DisplayName("Should return BadRequest when email is invalid")
        void shouldReturnBadRequest_whenEmailInvalid() throws Exception {
            SignUpRequest request = new SignUpRequest("email", password);

            mockMvc.perform(post("/v1/auth/signup")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message[0].error")
                            .value("must be a well-formed email address"));

            verify(authService, never()).signUp(any());
        }

        @Test
        @DisplayName("Should return BadRequest when password is blank")
        void shouldReturnBadRequest_whenPasswordIsBlank() throws Exception {
            SignUpRequest request = new SignUpRequest(email, "");

            mockMvc.perform(post("/v1/auth/signup")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message[0].error")
                            .value("must not be blank"));

            verify(authService, never()).signUp(any());
        }
    }

    @Nested
    class SignIn {

        @Test
        @DisplayName("Should return 200 and auth response on successful signin")
        void shouldReturn200OnSuccess() throws Exception {
            SignInRequest request = new SignInRequest(email, password);

            AuthResponse response = new AuthResponse(accessToken, refreshToken, tokenType, expiresIn);
            String expectedJson = objectMapper.writeValueAsString(response);
            when(authService.signIn(any(SignInRequest.class))).thenReturn(response);

            mockMvc.perform(post("/v1/auth/signin")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(content().json(expectedJson));

            verify(authService).signIn(any(SignInRequest.class));
        }

        @Test
        @DisplayName("Should return BadRequest when email is invalid")
        void shouldReturnBadRequest_whenEmailInvalid() throws Exception {
            SignInRequest request = new SignInRequest("email", password);

            mockMvc.perform(post("/v1/auth/signin")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message[0].error")
                            .value("must be a well-formed email address"));

            verify(authService, never()).signIn(any());
        }

        @Test
        @DisplayName("Should return BadRequest when password is blank")
        void shouldReturnBadRequest_whenPasswordIsBlank() throws Exception {
            SignInRequest request = new SignInRequest(email, "");

            mockMvc.perform(post("/v1/auth/signin")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message[0].error")
                            .value("must not be blank"));

            verify(authService, never()).signIn(any());
        }
    }

    @Nested
    class SignOut {

        @Test
        @DisplayName("Should return 204 on successful signout")
        void shouldReturn204OnSuccess() throws Exception {
            SignOutRequest request = new SignOutRequest(refreshToken);

            doNothing().when(authService).signOut(any(SignOutRequest.class));

            mockMvc.perform(post("/v1/auth/signout")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNoContent());

            verify(authService).signOut(any(SignOutRequest.class));
        }

        @Test
        @DisplayName("Should return BadRequest when refresh token is blank")
        void shouldReturnBadRequest_whenRefreshTokenBlank() throws Exception {
            SignOutRequest request = new SignOutRequest("");

            mockMvc.perform(post("/v1/auth/signout")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message[0].error")
                            .value("must not be blank"));

            verify(authService, never()).signOut(any());
        }
    }

    @Nested
    class RefreshToken {

        @Test
        @DisplayName("Should return 200 and auth response on successful token refresh")
        void shouldReturn200OnSuccess() throws Exception {
            RefreshTokenRequest request = new RefreshTokenRequest(refreshToken);

            AuthResponse response = new AuthResponse(accessToken, refreshToken, tokenType, expiresIn);
            String expectedJson = objectMapper.writeValueAsString(response);
            when(authService.refreshToken(any(RefreshTokenRequest.class))).thenReturn(response);

            mockMvc.perform(post("/v1/auth/refresh")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(content().json(expectedJson));

            verify(authService).refreshToken(any(RefreshTokenRequest.class));
        }

        @Test
        @DisplayName("Should return BadRequest when refresh token is blank")
        void shouldReturnBadRequest_whenRefreshTokenBlank() throws Exception {
            RefreshTokenRequest request = new RefreshTokenRequest("");

            mockMvc.perform(post("/v1/auth/refresh")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message[0].error")
                            .value("must not be blank"));

            verify(authService, never()).refreshToken(any());
        }
    }
}
