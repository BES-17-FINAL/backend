package com.example.backend.service;

import com.example.backend.dto.ChangePasswordRequest;
import com.example.backend.dto.LoginRequest;
import com.example.backend.dto.LoginResponse;
import com.example.backend.entity.User;
import com.example.backend.repository.UserRepository;
import com.example.backend.security.JwtUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final JwtUtil jwtUtil;

    public String signup(User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            return "이미 가입된 이메일입니다.";
        }
        user.setPassword(encoder.encode(user.getPassword()));
        user.setLoginType("LOCAL");
        userRepository.save(user);
        return "회원가입 완료";
    }

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("이메일이 존재하지 않습니다."));
        if (!encoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("비밀번호가 올바르지 않습니다.");
        }

        String token = jwtUtil.generateToken(user.getEmail());
        return new LoginResponse(token, user.getNickname(), user.getEmail());
    }
    @Transactional
    public void changePassword(String token, ChangePasswordRequest request) {

        // Authorization: Bearer XXX → XXX
        String jwt = token.replace("Bearer ", "");

        // 토큰에서 email 추출
        String email = jwtUtil.extractEmail(jwt);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        // 현재 비밀번호 검증
        if (!encoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new RuntimeException("현재 비밀번호가 일치하지 않습니다.");
        }

        // 새 비밀번호 암호화 후 저장
        user.setPassword(encoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

}