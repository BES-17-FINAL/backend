package com.example.backend.service;

import com.example.backend.dto.UpdateUserProfileRequest;
import com.example.backend.dto.UserProfileResponse;
import com.example.backend.entity.User;
import com.example.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final FileUploadService fileUploadService;
    public Long resolveUserId(Long userId, String email){
        if (userId != null) return userId;
        if (email == null) throw new IllegalArgumentException("UNAUTHORIZED");
        return userRepository.findByEmail(email)
                .map(User::getUserId)
                .orElseThrow(() -> new IllegalArgumentException("USER_NOT_FOUND"));
    }

    public UserProfileResponse me(Long userId){
        User u = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("USER_NOT_FOUND"));
        return UserProfileResponse.builder()
                .id(u.getUserId())
                .nickname(u.getNickname())
                .user_name(u.getUserName())
                .profile_img(u.getProfileImg())
                .build();
    }

    public void edit(Long userId, UpdateUserProfileRequest req){
        User u = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("USER_NOT_FOUND"));

        if (req.getNickname() != null)   u.setNickname(req.getNickname());
        if (req.getUser_name() != null)  u.setUserName(req.getUser_name());
        if (req.getProfile_img() != null) u.setProfileImg(req.getProfile_img());

        userRepository.save(u);
    }

    public void userProfileImage(MultipartFile file, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("USER_NOT_FOUND"));

        String image = user.getProfileImg();
        System.out.println(image != null);
        System.out.println(!image.isEmpty());
        System.out.println(file != null);
        System.out.println(!file.isEmpty());
        if (image != null && !image.isEmpty()) {
            fileUploadService.deleteImage(image);
        }
        if (file != null && !file.isEmpty()){
            String uploadedUrl = fileUploadService.uploadImage(file);
            user.setProfileImg(uploadedUrl);
            userRepository.save(user);
        }

    }
}