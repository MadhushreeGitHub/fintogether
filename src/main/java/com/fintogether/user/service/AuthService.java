package com.fintogether.user.service;

import com.fintogether.user.domain.RefreshToken;
import com.fintogether.user.domain.User;
import com.fintogether.user.dto.LoginRequest;
import com.fintogether.user.dto.LoginResponse;
import com.fintogether.user.repository.RefreshTokenRepository;
import com.fintogether.user.repository.UserRepository;
import com.fintogether.user.util.RefreshTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {
    private static final String DUMMY_HASH = new BCryptPasswordEncoder(12).encode("dummy-password-never-used");
    private static final long ACCESS_TOKEN_EXPIRY_SECONDS = 900;
    private static final long REFRESH_TOKEN_EXPIRY_DAYS = 30;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public LoginResponse login(LoginRequest request) {
        Optional<User> mayBeUser = userRepository.findByEmail(request.email());
        if (mayBeUser.isEmpty()) {
            passwordEncoder.matches(request.password(), DUMMY_HASH);
            throw new BadCredentialsException("Invalid credentials");
        }
        User user = mayBeUser.get();
        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new BadCredentialsException("Invalid credentials");
        }
        String token = jwtService.generateAccessToken(user);

        // Generate raw refresh token
        String refreshToken = RefreshTokenUtil.generateRawToken();
        String hashRefreshToken = RefreshTokenUtil.hashToken(refreshToken);

        // Persist refresh token (new family since this is a fresh login)
        RefreshToken refreshTokenEntity = RefreshToken.builder()
                .userId(user.getId())
                .tokenHash(hashRefreshToken)
                .familyId(UUID.randomUUID())
                .expiresAt(Instant.now().plus(REFRESH_TOKEN_EXPIRY_DAYS, ChronoUnit.DAYS))
                .build();
        refreshTokenRepository.save(refreshTokenEntity);

        return new LoginResponse(
                token,
                "Bearer",
                ACCESS_TOKEN_EXPIRY_SECONDS,
                user.getId(),
                user.getEmail(),
                refreshToken);
    }
}
