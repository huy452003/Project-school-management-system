package com.security.services;

import com.handle_exceptions.ConflictExceptionHandle;
import com.handle_exceptions.ForbiddenExceptionHandle;
import com.handle_exceptions.NotFoundExceptionHandle;
import com.handle_exceptions.UnauthorizedExceptionHandle;
import com.security.config.JwtConfig;
import com.security.entities.UserEntity;
import com.security.enums.Permission;
import com.security.enums.Role;
import com.security.models.Login;
import com.security.models.Register;
import com.security.models.SecurityResponse;
import com.security.models.TokenInfo;
import com.security.repositories.UserRepo;
import com.logging.services.LoggingService;
import com.logging.models.LogContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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
    @Autowired
    BlacklistService blacklistService;
    @Autowired
    ProfileOrchestrationService profileOrchestrationService;


    private LogContext getLogContext(String methodName) {
        return LogContext.builder()
                .module("security")
                .className(this.getClass().getSimpleName())
                .methodName(methodName)
                .build();
    }

    private String formatExpirationTime(Long expirationMillis) {
        LocalDateTime expirationTime = LocalDateTime.now().plusSeconds(expirationMillis / 1000);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return expirationTime.format(formatter);
    }

    @Transactional(rollbackFor = Exception.class)
    public SecurityResponse register(Register request){
        LogContext logContext = getLogContext("register");
        logContext.setUserId(request.getUsername());

        loggingService.logInfo("Register attempt for username: " + logContext.getUserId(), logContext);

        if(userRepo.existsByUserName(request.getUsername())){
            loggingService.logDebug("Username already exists", logContext);
            throw new ConflictExceptionHandle("", List.of(request.getUsername()) , "Security-Model");
        }

        // kiểm tra xem role ADMIN có tồn tại trong hệ thống không và chỉ chấp nhận 1 ADMIN
        Role requestedRole = Role.valueOf(request.getRole().toUpperCase());
        if (requestedRole == Role.ADMIN) {
            validateAdminRoleAssignment(logContext);
        }

        Role userRole = Role.valueOf(request.getRole().toUpperCase());
        UserEntity user = UserEntity.builder()
                .type(request.getType())
                .userName(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .age(request.getAge())
                .gender(request.getGender())
                .birth(request.getBirth())
                .role(userRole)
                .permissions(convertToPermissions(request.getPermissions(), userRole))
                .enabled(true)
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .build();

        userRepo.save(user);

        profileOrchestrationService.createProfile(user, request);

        // Tạo custom claims cho token
        Map<String, Object> claim = new HashMap<>();
        claim.put("role", user.getRole().name());
        claim.put("permissions", user.getPermissions().stream()
                .map(Enum::name)
                .collect(Collectors.toList()));
        
        String accessToken = jwtService.generateToken(claim, user);
        String refreshToken = jwtService.generateRefreshToken(claim, user);

        return SecurityResponse.builder()
                .userId(user.getUserId())
                .userName(user.getUsername())
                .age(user.getAge())
                .gender(user.getGender())
                .birth(user.getBirth())
                .role(user.getRole().name())
                .permissions(user.getPermissions().stream()
                        .map(Enum::name)
                        .collect(Collectors.toList()))
                .accessToken(accessToken)
                .expires(formatExpirationTime(jwtConfig.getExpiration()))
                .refreshToken(refreshToken)
                .refExpires(formatExpirationTime(jwtConfig.getRefreshExpiration()))
                .build();
    }

    public TokenInfo decodeToken(String token){
        LogContext logContext = getLogContext("decodeToken");
        try{
            TokenInfo tokenInfo = TokenInfo.builder()
                .username(jwtService.extractUsername(token))
                .role(jwtService.extractRole(token))
                .permissions(jwtService.extractPermissions(token))
                .expiration(formatExpirationTime(jwtService.extractExpiration(token).getTime()))
                .issuedAt(formatExpirationTime(jwtService.extractIssuedAt(token).getTime()))
                .jti(jwtService.extractJTI(token))
                .isExpired(jwtService.isTokenExpired(token))
                .build();

            loggingService.logInfo("Token decoded successfully", logContext);
            return tokenInfo;
        }catch(Exception e){
            loggingService.logError("Error decoding token", e, logContext);
            return null;
        }
    }

    public SecurityResponse login(Login request){
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        LogContext logContext = getLogContext("login");
        logContext.setUserId(request.getUsername());

        loggingService.logInfo("Login attempt for username: " + logContext.getUserId(), logContext);
        UserEntity user = userRepo.findByUserName(request.getUsername())
                .orElseThrow(() -> new NotFoundExceptionHandle(
                        "", List.of(request.getUsername()), "Security-Model")
                );

        // Tạo custom claims cho token
        Map<String, Object> claim = new HashMap<>();
        claim.put("role", user.getRole().name());
        claim.put("permissions", user.getPermissions().stream()
                .map(Enum::name)
                .collect(Collectors.toList()));
        
        String accessToken = jwtService.generateToken(claim, user);
        String refreshToken = jwtService.generateRefreshToken(claim, user);

        return SecurityResponse.builder()
                .userId(user.getUserId())
                .userName(user.getUsername())
                .age(user.getAge())
                .gender(user.getGender())
                .birth(user.getBirth())
                .role(user.getRole().name())
                .permissions(user.getPermissions().stream()
                        .map(Enum::name)
                        .collect(Collectors.toList()))
                .accessToken(accessToken)
                .expires(formatExpirationTime(jwtConfig.getExpiration()))
                .refreshToken(refreshToken)
                .refExpires(formatExpirationTime(jwtConfig.getRefreshExpiration()))
                .build();
    }

    public Map<String, Object> logout(String token) {
        LogContext logContext = getLogContext("logout");
        
        String username = getUsernameFromToken(token);
            
            // Blacklist tất cả token của user (logout all devices)
            blacklistService.blacklistAllUserTokens(username);
            
            Map<String, Object> logoutResponse = new HashMap<>();
            logoutResponse.put("username", username);
            logoutResponse.put("timestamp",
            java.time.LocalDateTime.now()
            .format(java.time.format.DateTimeFormatter
            .ofPattern("yyyy-MM-dd HH:mm:ss")));
            
            loggingService.logInfo("User logged out from all devices successfully: " + username
            , logContext);
            return logoutResponse;
    }

    public SecurityResponse refreshToken(String authHeader){
        LogContext logContext = getLogContext("refreshToken");

        String refreshToken = authHeader.substring(7); 
        
        String username = jwtService.extractUsername(refreshToken);

        // Kiểm tra user token có bị blacklist không
        if (blacklistService.isUserTokenBlacklisted(refreshToken, username)) {
            loggingService.logDebug("User token is blacklisted", logContext);
            throw new UnauthorizedExceptionHandle("User token is blacklisted"
            , "User has been logged out from all devices");
        }

        UserEntity user = userRepo.findByUserName(username)
                .orElseThrow(() -> new NotFoundExceptionHandle(
                        "", List.of(username),"Security-Model")
                );
        if (!jwtService.isTokenValid(refreshToken,user)){
            throw new UnauthorizedExceptionHandle("Invalid refresh token"
            , "Refresh token is expired");
        }

        // Tạo custom claims cho new access token
        Map<String, Object> accessClaims = new HashMap<>();
        accessClaims.put("role", user.getRole().name());
        accessClaims.put("permissions", user.getPermissions().stream()
                .map(Enum::name)
                .collect(Collectors.toList()));
        
        String newAccessToken = jwtService.generateToken(accessClaims, user);

        return SecurityResponse.builder()
                .userId(user.getUserId())
                .userName(user.getUsername())
                .age(user.getAge())
                .gender(user.getGender())
                .birth(user.getBirth())
                .role(user.getRole().name())
                .permissions(user.getPermissions().stream()
                        .map(Enum::name)
                        .collect(Collectors.toList()))
                .accessToken(newAccessToken)
                .expires(formatExpirationTime(jwtConfig.getExpiration()))
                .refreshToken(refreshToken)
                .refExpires(formatExpirationTime(jwtService.extractExpiration(refreshToken).getTime()))
                .build();
    }

    public String getUsernameFromToken(String token) {
        try {
            return jwtService.extractUsername(token);
        } catch (Exception e) {
            loggingService.logError("Error extracting username from token", e, getLogContext("getUsernameFromToken"));
            throw new UnauthorizedExceptionHandle("Error extracting username from token");
        }
    }

    private void validateAdminRoleAssignment(LogContext logContext) {
        // Kiểm tra xem đã có ADMIN nào trong hệ thống chưa
        long adminCount = userRepo.countByRole(Role.ADMIN);
        
        if (adminCount > 0) {
            loggingService.logWarn("Attempt to create additional ADMIN user - only one ADMIN allowed", logContext);
            throw new ForbiddenExceptionHandle(
                "ADMIN role assignment restricted", 
                "Only one ADMIN user is allowed in the system");
        }
        
        loggingService.logInfo("First ADMIN user being created - this is allowed", logContext);
    }

    private Set<Permission> convertToPermissions(List<String> permissionStrings, Role role) {
        Set<Permission> permissions = new HashSet<>();
        
        // ADMIN tự động có tất cả permissions
        if (role == Role.ADMIN) {
            permissions.addAll(List.of(Permission.values()));
            loggingService.logDebug("ADMIN role assigned all permissions automatically", getLogContext("convertToPermissions"));
            return permissions;
        }
        
        // Các role khác cần permissions cụ thể
        if (permissionStrings != null && !permissionStrings.isEmpty()) {
            for (String permissionStr : permissionStrings) {
                try {
                    permissions.add(Permission.valueOf(permissionStr.toUpperCase()));
                } catch (IllegalArgumentException e) {
                    loggingService.logWarn("Invalid permission: " + permissionStr, getLogContext("convertToPermissions"));
                }
            }
        }
        return permissions;
    }

    
}
