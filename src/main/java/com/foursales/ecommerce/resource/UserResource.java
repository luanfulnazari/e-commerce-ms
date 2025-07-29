package com.foursales.ecommerce.resource;

import com.foursales.ecommerce.enums.Role;
import com.foursales.ecommerce.resource.response.UserResponse;
import com.foursales.ecommerce.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
public class UserResource {

    private final UserService userService;

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/promote")
    @ResponseStatus(HttpStatus.OK)
    public UserResponse promoteToAdmin(@PathVariable UUID id) {
        return userService.promoteToAdmin(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public UserResponse getUserById(@PathVariable UUID id) {
        return userService.getUserById(id);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Page<UserResponse> getAllUsers(
            @PageableDefault(size = 20, sort = "email") Pageable pageable,
            @RequestParam(required = false) Role role) {
        return userService.getAllUsers(pageable, role);
    }

    @GetMapping("/me")
    public UserResponse getAuthenticatedUser() {
        return userService.getAuthenticatedUser();
    }
}

