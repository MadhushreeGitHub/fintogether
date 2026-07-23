package com.fintogether.user.service;

import com.fintogether.user.domain.User;
import com.fintogether.user.dto.SignupRequest;
import com.fintogether.user.dto.UserResponse;
import com.fintogether.user.exception.EmailAlreadyExistsException;
import com.fintogether.user.exception.PhoneAlreadyExistsException;
import com.fintogether.user.exception.UsernameAlreadyExistsException;
import com.fintogether.user.repository.UserRepository;
import com.fintogether.user.util.Normalizer;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    @Transactional
    public UserResponse signup(SignupRequest request) {
        String email = Normalizer.email(request.email());
        String phone = Normalizer.phone(request.phone());

        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException(email);

        }

        if (userRepository.existsByPhone(phone)) {
            throw new PhoneAlreadyExistsException(phone);
        }

        if (userRepository.existsByUsername(request.username())) {
            throw new UsernameAlreadyExistsException(request.username());
        }
        String hashedPassword = passwordEncoder.encode(request.password());
        User newUser = User.builder()
                .email(email)
                .phone(phone)
                .username(request.username())
                .passwordHash(hashedPassword)
                .role(request.role())
                .build();

        newUser = userRepository.save(newUser);

        return new UserResponse(
                newUser.getId(),
                newUser.getEmail(),
                newUser.getPhone(),
                newUser.getUsername(),
                newUser.getRole(),
                newUser.getCreatedAt()
        );

    }

}
