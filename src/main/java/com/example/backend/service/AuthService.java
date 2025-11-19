package com.example.backend.service;

import com.example.backend.dto.LoginRequest;
import com.example.backend.dto.LoginResponse;
import com.example.backend.entity.User;
import com.example.backend.repository.UserRepository;
import com.example.backend.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final JwtUtil jwtUtil;

    // ------------------- 회원가입 -------------------
    public String signup(User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            return "이미 가입된 이메일입니다.";
        }
        user.setPassword(encoder.encode(user.getPassword()));
        user.setLoginType("LOCAL");
        userRepository.save(user);
        return "회원가입 완료";
    }

    // ------------------- 로그인 -------------------
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("이메일이 존재하지 않습니다."));
        if (!encoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("비밀번호가 올바르지 않습니다.");
        }

        String token = jwtUtil.generateToken(user.getEmail());
        return new LoginResponse(token, user.getNickname(), user.getEmail());
    }

    // ------------------- 토큰 검증 -------------------
    public boolean validateToken(String token) {
        return jwtUtil.validateToken(token);
    }

    // ------------------- 토큰에서 이메일 추출 -------------------
    public String getEmailFromToken(String token) {
        return jwtUtil.extractEmail(token);
    }

    // ------------------- 이메일로 유저 조회 -------------------
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
