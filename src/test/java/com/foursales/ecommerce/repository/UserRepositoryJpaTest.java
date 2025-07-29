package com.foursales.ecommerce.repository;

import com.foursales.ecommerce.entity.User;
import com.foursales.ecommerce.enums.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryJpaTest {

    @Autowired
    private UserRepository userRepository;

    @Nested
    class FindByEmailTests {

        @Test
        @DisplayName("Should find user by email when user exists")
        void shouldFindUserByEmail() {
            createUser("test@example.com", Role.USER);

            Optional<User> result = userRepository.findByEmail("test@example.com");

            assertTrue(result.isPresent());
        }
    }

    @Nested
    class ExistsByEmailTests {

        @Test
        @DisplayName("Should return true when email exists")
        void shouldReturnTrueWhenEmailExists() {
            createUser("exists@example.com", Role.USER);

            boolean exists = userRepository.existsByEmail("exists@example.com");

            assertTrue(exists);
        }

        @Test
        @DisplayName("Should return false when email does not exist")
        void shouldReturnFalseWhenEmailDoesNotExist() {
            boolean exists = userRepository.existsByEmail("notfound@example.com");

            assertFalse(exists);
        }
    }

    @Nested
    class FindAllByRoleTests {

        @Test
        @DisplayName("Should return users with admin role")
        void shouldReturnUsersWithAdminRole() {
            createUser("admin1@example.com", Role.ADMIN);
            createUser("admin2@example.com", Role.ADMIN);
            createUser("user1@example.com", Role.USER);

            Pageable pageable = PageRequest.of(0, 10);
            Page<User> result = userRepository.findAllByRole(Role.ADMIN, pageable);

            assertEquals(2, result.getTotalElements());
            assertTrue(result.stream().allMatch(user -> user.getRole().equals(Role.ADMIN)));
        }

        @Test
        @DisplayName("Should return users with user role")
        void shouldReturnUsersWithUserRole() {
            createUser("admin1@example.com", Role.ADMIN);
            createUser("admin2@example.com", Role.ADMIN);
            createUser("user1@example.com", Role.USER);

            Pageable pageable = PageRequest.of(0, 10);
            Page<User> result = userRepository.findAllByRole(Role.USER, pageable);

            assertEquals(1, result.getTotalElements());
            assertTrue(result.stream().allMatch(user -> user.getRole().equals(Role.USER)));
        }
    }

    private void createUser(String email, Role role) {
        User user = new User();
        user.setEmail(email);
        user.setPassword("password");
        user.setRole(role);
        userRepository.save(user);
    }
}
