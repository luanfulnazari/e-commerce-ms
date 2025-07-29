package com.foursales.ecommerce.resource;

import com.foursales.ecommerce.resource.request.RefreshTokenRequest;
import com.foursales.ecommerce.resource.request.SignInRequest;
import com.foursales.ecommerce.resource.request.SignOutRequest;
import com.foursales.ecommerce.resource.request.SignUpRequest;
import com.foursales.ecommerce.resource.response.AuthResponse;
import com.foursales.ecommerce.resource.response.UserResponse;
import com.foursales.ecommerce.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("v1/auth")
@RequiredArgsConstructor
public class AuthResource {

    private final AuthService authService;

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse signUp(
            @RequestBody @Valid SignUpRequest request) {
        return authService.signUp(request);
    }

    @PostMapping("/signin")
    @ResponseStatus(HttpStatus.OK)
    public AuthResponse signIn(
            @RequestBody @Valid SignInRequest request) {
        return authService.signIn(request);
    }

    @PostMapping("/signout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void signOut(
            @RequestBody @Valid SignOutRequest request) {
        authService.signOut(request);
    }

    @PostMapping("/refresh")
    @ResponseStatus(HttpStatus.OK)
    public AuthResponse refresh(
            @RequestBody @Valid RefreshTokenRequest request) {
        return authService.refreshToken(request);
    }
}
