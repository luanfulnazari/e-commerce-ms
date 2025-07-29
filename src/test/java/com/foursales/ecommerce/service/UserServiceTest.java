package com.foursales.ecommerce.service;

import com.foursales.ecommerce.entity.User;
import com.foursales.ecommerce.enums.Role;
import com.foursales.ecommerce.repository.UserRepository;
import com.foursales.ecommerce.resource.response.UserResponse;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private UserService userService;

    private User user;
    private User admin;

    private final UUID userId = UUID.randomUUID();
    private final UUID adminId = UUID.randomUUID();

    @BeforeEach
    void setup() {
        user = User.builder()
                .id(userId)
                .email("user@example.com")
                .role(Role.USER)
                .build();

        admin = User.builder()
                .id(adminId)
                .email("admin@example.com")
                .role(Role.ADMIN)
                .build();
    }

    @Nested
    class PromoteToAdmin {

        @Test
        @DisplayName("Should promote user to ADMIN and return updated user response")
        void shouldPromoteUserToAdmin() {
            when(userRepository.findById(userId)).thenReturn(Optional.of(user));

            UserResponse response = userService.promoteToAdmin(userId);

            assertEquals(user.getId(), response.getId());
            assertEquals(user.getEmail(), response.getEmail());
            assertEquals(Role.ADMIN, response.getRole());

            verify(userRepository).findById(userId);
            verifyNoMoreInteractions(userRepository);
        }

        @Test
        @DisplayName("Should throw EntityNotFoundException when user does not exist")
        void shouldThrowExceptionWhenUserNotFound() {
            when(userRepository.findById(userId)).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class,
                    () -> userService.promoteToAdmin(userId));

            verify(userRepository).findById(userId);
            verifyNoMoreInteractions(userRepository);
        }
    }

    @Nested
    class GetAuthenticatedUser {

        @Test
        @DisplayName("Should return the currently authenticated user")
        void shouldReturnAuthenticatedUser() {
            when(jwtService.getAuthenticatedUserId()).thenReturn(userId);
            when(userRepository.findById(userId)).thenReturn(Optional.of(user));

            UserResponse actualResponse = userService.getAuthenticatedUser();

            assertEquals(user.getId(), actualResponse.getId());
            assertEquals(user.getEmail(), actualResponse.getEmail());
            assertEquals(user.getRole(), actualResponse.getRole());

            verify(jwtService).getAuthenticatedUserId();
            verify(userRepository).findById(userId);
            verifyNoMoreInteractions(jwtService, userRepository);
        }

        @Test
        @DisplayName("Should throw EntityNotFoundException when authenticated user is not found")
        void shouldThrowWhenUserNotFound() {
            when(jwtService.getAuthenticatedUserId()).thenReturn(userId);
            when(userRepository.findById(userId)).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class, () -> userService.getAuthenticatedUser());

            verify(jwtService).getAuthenticatedUserId();
            verify(userRepository).findById(userId);
            verifyNoMoreInteractions(jwtService, userRepository);
        }
    }

    @Nested
    class GetUserById {

        @Test
        @DisplayName("Should return user when found by ID")
        void shouldReturnUserById() {
            when(userRepository.findById(userId)).thenReturn(Optional.of(user));

            UserResponse response = userService.getUserById(userId);

            assertEquals(userId, response.getId());
            assertEquals(user.getEmail(), response.getEmail());
            assertEquals(user.getRole(), response.getRole());

            verify(userRepository).findById(userId);
            verifyNoMoreInteractions(userRepository);
        }

        @Test
        @DisplayName("Should throw EntityNotFoundException when user is not found by ID")
        void shouldThrowWhenUserByIdNotFound() {
            when(userRepository.findById(userId)).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class, () -> userService.getUserById(userId));

            verify(userRepository).findById(userId);
            verifyNoMoreInteractions(userRepository);
        }
    }

    @Nested
    class GetAllUsers {

        @Test
        @DisplayName("Should return paginated users without filtering by role")
        void shouldReturnUsersWithoutRoleFilter() {
            Pageable pageable = PageRequest.of(0, 10, Sort.by("email").ascending());
            Page<User> userPage = new PageImpl<>(List.of(user, admin), pageable, 2);

            when(userRepository.findAll(pageable)).thenReturn(userPage);

            Page<UserResponse> result = userService.getAllUsers(pageable, null);

            assertEquals(2, result.getTotalElements());
            assertEquals(user.getEmail(), result.getContent().get(0).getEmail());
            assertEquals(admin.getEmail(), result.getContent().get(1).getEmail());
            assertTrue(result.getContent().stream().anyMatch(u -> u.getRole() == Role.ADMIN));
            assertTrue(result.getContent().stream().anyMatch(u -> u.getRole() == Role.USER));

            verify(userRepository).findAll(pageable);
            verifyNoMoreInteractions(userRepository);
        }

        @Test
        @DisplayName("Should return paginated users filtered by role")
        void shouldReturnUsersFilteredByRole() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<User> userPage = new PageImpl<>(List.of(user), pageable, 1);

            when(userRepository.findAllByRole(Role.USER, pageable)).thenReturn(userPage);

            Page<UserResponse> result = userService.getAllUsers(pageable, Role.USER);

            assertEquals(1, result.getTotalElements());

            UserResponse userResponse = result.getContent().get(0);
            assertEquals(userId, userResponse.getId());
            assertEquals(Role.USER, userResponse.getRole());
            assertEquals(user.getEmail(), userResponse.getEmail());

            verify(userRepository).findAllByRole(Role.USER, pageable);
            verifyNoMoreInteractions(userRepository);
        }
    }
}
