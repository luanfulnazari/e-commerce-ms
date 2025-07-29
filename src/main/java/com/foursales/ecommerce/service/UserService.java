package com.foursales.ecommerce.service;

import com.foursales.ecommerce.entity.User;
import com.foursales.ecommerce.enums.Role;
import com.foursales.ecommerce.mapper.UserMapper;
import com.foursales.ecommerce.repository.UserRepository;
import com.foursales.ecommerce.resource.response.UserResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final JwtService jwtService;

    @Transactional
    public UserResponse promoteToAdmin(UUID userId) {
        return userRepository.findById(userId).map(user -> {
            user.setRole(Role.ADMIN);
            return UserMapper.toResponse(user);
        }).orElseThrow(() -> new EntityNotFoundException("User not found: " + userId));
    }

    @Transactional(readOnly = true)
    public UserResponse getUserById(UUID userId) {
        return userRepository.findById(userId)
                .map(UserMapper::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + userId));
    }

    @Transactional(readOnly = true)
    public Page<UserResponse> getAllUsers(Pageable pageable, Role role) {
        Page<User> users = (role != null)
                ? userRepository.findAllByRole(role, pageable)
                : userRepository.findAll(pageable);
        return users.map(UserMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public UserResponse getAuthenticatedUser() {
        UUID userId = jwtService.getAuthenticatedUserId();
        return userRepository.findById(userId)
                .map(UserMapper::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("User not found :" + userId));
    }
}
