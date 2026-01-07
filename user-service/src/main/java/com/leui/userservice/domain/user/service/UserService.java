package com.leui.userservice.domain.user.service;

import com.leui.userservice.domain.user.dto.UserCreateRequest;
import com.leui.userservice.domain.user.entity.User;
import com.leui.userservice.domain.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public Long createUser(UserCreateRequest request) {
        User user = new User(request.email(), passwordEncoder.encode(request.password()), request.role());
        return userRepository.save(user).getId();
    }

    public User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found. email = " + email));
    }

    public User getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found. id = " + id));
    }

}
