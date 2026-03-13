package com.theruzil.auth.service;

import com.theruzil.auth.dto.LoginResponse;
import com.theruzil.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    public LoginResponse buildResponse(String username) {
        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("User not found: " + username));
        return new LoginResponse(user.getId(), user.getUsername());
    }
}
