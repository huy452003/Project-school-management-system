package com.security.services;

import com.handle_exceptions.ConflictExceptionHandle;
import com.handle_exceptions.NotFoundExceptionHandle;
import com.security.config.JwtConfig;
import com.security.entities.Role;
import com.security.entities.UserEntity;
import com.security.models.Login;
import com.security.models.Register;
import com.security.models.SecurityResponse;
import com.security.repositories.UserRepo;
import com.logging.services.LoggingService;
import com.logging.models.LogContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class AuthService {
    @Autowired
    UserRepo userRepo;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    JwtService jwtService;
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    JwtConfig jwtConfig;
    @Autowired
    LoggingService loggingService;


    private LogContext getLogContext(String methodName) {
        return LogContext.builder()
                .module("security")
                .className("AuthService")
                .methodName(methodName)
                .build();
    }

    private String formatExpirationTime(Long expirationMillis) {
        LocalDateTime expirationTime = LocalDateTime.now().plusSeconds(expirationMillis / 1000);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return expirationTime.format(formatter);
    }

    public SecurityResponse register(Register request){
        LogContext logContext = getLogContext("register");
        loggingService.logInfo("Register attempt for username: " + request.getUsername(), logContext);

        if(userRepo.existsByUserName(request.getUsername())){
            loggingService.logExceptionHandled("ConflictExceptionHandle", "Username already exists", logContext);
            throw new ConflictExceptionHandle("", List.of(request.getUsername()));
        }

        UserEntity user = UserEntity.builder()
                .userName(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .role(Role.valueOf(request.getRole().toUpperCase()))
                .enabled(true)
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .build();

        userRepo.save(user);

        String accessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return SecurityResponse.builder()
                .userName(user.getUsername())
                .role(user.getRole().name())
                .accessToken(accessToken)
                .expires(formatExpirationTime(jwtConfig.getExpiration()))
                .refreshToken(refreshToken)
                .refExpires(formatExpirationTime(jwtConfig.getRefreshExpiration()))
                .build();
    }

    public SecurityResponse login(Login request){
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        UserEntity user = userRepo.findByUserName(request.getUsername())
                .orElseThrow(() -> new NotFoundExceptionHandle(
                        "", List.of(request.getUsername()), null)
                );

        String accessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return SecurityResponse.builder()
                .userName(user.getUsername())
                .role(user.getRole().name())
                .accessToken(accessToken)
                .expires(formatExpirationTime(jwtConfig.getExpiration()))
                .refreshToken(refreshToken)
                .refExpires(formatExpirationTime(jwtConfig.getRefreshExpiration()))
                .build();
    }

    public SecurityResponse refreshToken(String authHeader){
        String refreshToken = authHeader;
        if (authHeader.startsWith("Bearer ")) {
            refreshToken = authHeader.substring(7);
        }
        
        String username = jwtService.extractUsername(refreshToken);

        UserEntity user = userRepo.findByUserName(username)
                .orElseThrow(() -> new NotFoundExceptionHandle(
                        "", List.of(username),null)
                );
        if (!jwtService.isTokenValid(refreshToken,user)){
            throw new RuntimeException("Invalid refresh token");
        }

        String newAccessToken = jwtService.generateToken(user);

        return SecurityResponse.builder()
                .userName(user.getUsername())
                .role(user.getRole().name())
                .accessToken(newAccessToken)
                .expires(formatExpirationTime(jwtConfig.getExpiration()))
                .refreshToken(refreshToken)
                .refExpires(formatExpirationTime(jwtService.extractExpiration(refreshToken).getTime()))
                .build();
    }
}
