package com.example.musicupc.controllers;

import com.example.musicupc.dtos.*;
import com.example.musicupc.security.JwtUtil;
import org.springframework.security.authentication.*;
import org.springframework.web.bind.annotation.*;

@RestController
public class AuthController {
    private final AuthenticationManager authManager;

    private final JwtUtil jwtUtil;

    public AuthController(
            AuthenticationManager authManager,
            JwtUtil jwtUtil
    ) {

        this.authManager = authManager;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public LoginResponseDTO login(
            @RequestBody LoginRequestDTO request
    ) {

        authManager.authenticate(

                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        String token =
                jwtUtil.generateToken(
                        request.getUsername()
                );

        return new LoginResponseDTO(token);
    }
}
