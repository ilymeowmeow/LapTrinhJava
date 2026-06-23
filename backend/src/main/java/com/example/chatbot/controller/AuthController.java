package com.example.chatbot.controller;

import com.example.chatbot.entity.User;
import com.example.chatbot.repository.UserRepository;
import com.example.chatbot.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @PostMapping("/sync")
    public ResponseEntity<Map<String, String>> syncUser(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        String name = payload.get("name");
        String image = payload.get("image");

        if (email == null || email.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email is required"));
        }

        Optional<User> optionalUser = userRepository.findByEmail(email);
        User user;

        if (optionalUser.isPresent()) {
            user = optionalUser.get();
            // Update info if changed
            user.setName(name);
            user.setAvatarUrl(image);
            user = userRepository.save(user);
        } else {
            // Create new user
            user = User.builder()
                    .email(email)
                    .name(name)
                    .avatarUrl(image)
                    .role("ROLE_USER")
                    .build();
            user = userRepository.save(user);
        }

        // Generate JWT
        String token = jwtUtil.generateToken(user.getId(), user.getEmail());
        
        return ResponseEntity.ok(Map.of("token", token));
    }
}
