package com.foursales.ecommerce.mapper;


import com.foursales.ecommerce.entity.User;
import com.foursales.ecommerce.enums.Role;
import com.foursales.ecommerce.resource.response.UserResponse;

public class UserMapper {

    public static User toEntity(String email, String encryptedPassword, Role role) {
        return User.builder()
                .email(email)
                .password(encryptedPassword)
                .role(role)
                .build();
    }

    public static UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }
}
