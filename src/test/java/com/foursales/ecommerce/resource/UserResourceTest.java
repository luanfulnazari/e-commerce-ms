package com.foursales.ecommerce.resource;

import com.foursales.ecommerce.enums.Role;
import com.foursales.ecommerce.resource.response.UserResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserResource.class)
class UserResourceTest extends AbstractResourceMvcTest {

    private final UUID userId = UUID.randomUUID();
    private final String userEmail = "user@email.com";
    private final Role userRole = Role.USER;
    private final UUID adminId = UUID.randomUUID();
    private final String adminEmail = "admin@email.com";
    private final Role adminRole = Role.ADMIN;

    @Nested
    class PromoteToAdmin {

        @Test
        @DisplayName("Should return 200 and user response when admin promotes a user")
        void shouldReturn200_whenAdminPromotesUser() throws Exception {
            UserResponse response = new UserResponse(adminId, adminEmail, adminRole);
            String expectedJson = objectMapper.writeValueAsString(response);
            when(userService.promoteToAdmin(userId)).thenReturn(response);

            mockMvc.perform(patch("/v1/users/{id}/promote", userId))
                    .andExpect(status().isOk())
                    .andExpect(content().json(expectedJson));

            verify(userService).promoteToAdmin(userId);
        }
    }

    @Nested
    class GetUserById {

        @Test
        @DisplayName("Should return 200 and user by id")
        void shouldReturnUserByIdForAdmin() throws Exception {
            UserResponse response = new UserResponse(userId, userEmail, userRole);
            String expectedJson = objectMapper.writeValueAsString(response);
            when(userService.getUserById(userId)).thenReturn(response);

            mockMvc.perform(get("/v1/users/{id}", userId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().json(expectedJson));

            verify(userService).getUserById(userId);
        }
    }

    @Nested
    class GetAllUsers {

        @Test
        @DisplayName("Should return 200 paginated list of users")
        void shouldReturnPaginatedSortedUsers() throws Exception {
            UserResponse user = new UserResponse(userId, userEmail, userRole);
            UserResponse admin = new UserResponse(adminId, adminEmail, adminRole);

            Page<UserResponse> page = new PageImpl<>(List.of(user, admin));
            String expectedJson = objectMapper.writeValueAsString(page);
            when(userService.getAllUsers(any(Pageable.class), any())).thenReturn(page);

            mockMvc.perform(get("/v1/users")
                            .param("page", "0")
                            .param("size", "10")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().json(expectedJson));

            verify(userService).getAllUsers(any(Pageable.class), any());
        }

        @Test
        @DisplayName("Should return 200 paginated list of users with admin role")
        void shouldReturnPaginatedSortedUsersWithAdminRole() throws Exception {
            UserResponse admin = new UserResponse(adminId, adminEmail, adminRole);

            Page<UserResponse> page = new PageImpl<>(List.of(admin));
            String expectedJson = objectMapper.writeValueAsString(page);
            when(userService.getAllUsers(any(Pageable.class), any())).thenReturn(page);

            mockMvc.perform(get("/v1/users")
                            .param("page", "0")
                            .param("size", "10")
                            .param("role", "ADMIN")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().json(expectedJson));

            verify(userService).getAllUsers(any(Pageable.class), any());
        }

        @Test
        @DisplayName("Should return 200 paginated list of users with user role")
        void shouldReturnPaginatedSortedUsersWithUserRole() throws Exception {
            UserResponse user = new UserResponse(userId, userEmail, userRole);

            Page<UserResponse> page = new PageImpl<>(List.of(user));
            String expectedJson = objectMapper.writeValueAsString(page);
            when(userService.getAllUsers(any(Pageable.class), any())).thenReturn(page);

            mockMvc.perform(get("/v1/users")
                            .param("page", "0")
                            .param("size", "10")
                            .param("role", "USER")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().json(expectedJson));

            verify(userService).getAllUsers(any(Pageable.class), any());
        }
    }

    @Nested
    class GetAuthenticatedUser {

        @Test
        @DisplayName("Should return 200 and user authenticated")
        void shouldReturnAuthenticatedUser() throws Exception {
            UserResponse response = new UserResponse(userId, userEmail, userRole);
            String expectedJson = objectMapper.writeValueAsString(response);
            when(userService.getAuthenticatedUser()).thenReturn(response);

            mockMvc.perform(get("/v1/users/me")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().json(expectedJson));

            verify(userService).getAuthenticatedUser();
        }
    }
}
