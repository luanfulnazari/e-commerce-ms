package com.foursales.ecommerce.repository;

import com.foursales.ecommerce.entity.User;
import com.foursales.ecommerce.enums.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
            createUser("test@example.com");

            Optional<User> result = userRepository.findByEmail("test@example.com");

            assertTrue(result.isPresent());
        }
    }

    @Nested
    class ExistsByEmailTests {

        @Test
        @DisplayName("Should return true when email exists")
        void shouldReturnTrueWhenEmailExists() {
            createUser("exists@example.com");

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

    private void createUser(String email) {
        User user = new User();
        user.setEmail(email);
        user.setPassword("password");
        user.setRole(Role.USER);
        userRepository.save(user);
    }
}
