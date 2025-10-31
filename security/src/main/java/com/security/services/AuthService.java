package com.security.services;

import com.handle_exceptions.ConflictExceptionHandle;
import com.handle_exceptions.ForbiddenExceptionHandle;
import com.handle_exceptions.NotFoundExceptionHandle;
import com.handle_exceptions.UnauthorizedExceptionHandle;
import com.security.config.JwtConfig;
import com.security.entities.UserEntity;
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
import com.kafka_shared.services.KafkaProducerService;
import com.kafka_shared.models.UserEvent;
import com.model_shared.models.user.UserDto;
import com.model_shared.enums.Permission;
import com.model_shared.enums.Role;
import com.model_shared.enums.Type;
import com.model_shared.enums.Status;
import org.modelmapper.ModelMapper;

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
    KafkaProducerService kafkaProducerService;
    @Autowired
    ModelMapper modelMapper;


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

        // Kiểm tra username đã tồn tại chưa
        UserEntity existingUser = null;
        if(userRepo.existsByUsername(request.getUsername())){
            // Kiểm tra xem user có status FAILED không, nếu có thì có thể cho phép register lại
            existingUser = userRepo.findByUsername(request.getUsername()).orElse(null);
            if (existingUser != null && existingUser.getStatus() == Status.FAILED) {
                loggingService.logInfo("Username exists with FAILED status, allowing re-registration by reusing userId: " 
                    + existingUser.getUserId(), logContext);
            } else {
                loggingService.logDebug("Username already exists with active status", logContext);
                throw new ConflictExceptionHandle("", List.of(request.getUsername()) , "Security-Model");
            }
        }

        // kiểm tra xem role ADMIN có tồn tại trong hệ thống không và chỉ chấp nhận 1 ADMIN
        Role requestedRole = Role.valueOf(request.getRole().toUpperCase());
        if (requestedRole == Role.ADMIN) {
            validateAdminRoleAssignment(logContext);
        }

        Role userRole = Role.valueOf(request.getRole().toUpperCase());
        
        UserEntity user;
        if (existingUser != null) {
            // Re-registration: UPDATE user FAILED thay vì tạo mới
            // Giữ nguyên userId để tránh lủng lỗ
            user = existingUser;
            user.setType(request.getType());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setFirstName(request.getFirstName());
            user.setLastName(request.getLastName());
            user.setAge(request.getAge());
            user.setGender(request.getGender());
            user.setBirth(request.getBirth());
            user.setRole(userRole);
            user.setPermissions(convertToPermissions(request.getPermissions(), userRole));
            user.setStatus(Status.PENDING);
            
            loggingService.logInfo("Reusing existing userId: " + user.getUserId() + " for re-registration", logContext);
        } else {
            // New registration: Tạo user mới
            user = UserEntity.builder()
                    .type(request.getType())
                    .username(request.getUsername())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .firstName(request.getFirstName())
                    .lastName(request.getLastName())
                    .age(request.getAge())
                    .gender(request.getGender())
                    .birth(request.getBirth())
                    .role(userRole)
                    .permissions(convertToPermissions(request.getPermissions(), userRole))
                    .status(Status.PENDING)
                    .build();
        }

        userRepo.save(user);

        Map<String, Object> profileData = new HashMap<>();
        if(request.getType() == Type.STUDENT){
            profileData.put("graduate", (Boolean) request.getProfileData().get("graduate"));
        } else {
            profileData = null;
        }

        // Convert UserEntity → UserDto using ModelMapper
        UserDto userDto = modelMapper.map(user, UserDto.class);
        
        // Set custom field (profileData - không có trong UserEntity)
        userDto.setProfileData(profileData);

        UserEvent userEvent = UserEvent.userRegistered(userDto);
        kafkaProducerService.sendUserEvent(userEvent);

        // Tạo custom claims cho token
        Map<String, Object> claim = new HashMap<>();
        claim.put("role", user.getRole().name());
        claim.put("permissions", user.getPermissions().stream()
                .map(Permission::name)
                .collect(Collectors.toSet()));
        
        String accessToken = jwtService.generateToken(claim, user);
        String refreshToken = jwtService.generateRefreshToken(claim, user);

        return SecurityResponse.builder()
                .status(user.getStatus())
                .userId(user.getUserId())
                .username(user.getUsername())
                .age(user.getAge())
                .gender(user.getGender())
                .birth(user.getBirth())
                .role(user.getRole())
                .permissions(user.getPermissions())
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
        LogContext logContext = getLogContext("login");
        logContext.setUserId(request.getUsername());

        loggingService.logInfo("Login attempt for username: " + logContext.getUserId(), logContext);
        UserEntity user = userRepo.findByUsername(request.getUsername())
                .orElseThrow(() -> new NotFoundExceptionHandle(
                        "", List.of(request.getUsername()), "Security-Model")
                );

        // Kiểm tra status của user
        if (user.getStatus() == Status.FAILED) {
            loggingService.logWarn("Login attempt for user with FAILED status: " + user.getUsername(), logContext);
            throw new ForbiddenExceptionHandle(
                "Account creation failed",
                "Your account creation process failed. Please contact administrator or register again."
            );
        } else if (user.getStatus() == Status.PENDING) {
            loggingService.logWarn("Login attempt for user with PENDING status: " + user.getUsername(), logContext);
            throw new ForbiddenExceptionHandle(
                "Account pending",
                "Your account is still being created. Please wait a moment and try again."
            );
        } else if (user.getStatus() == Status.DISABLED) {
            loggingService.logWarn("Login attempt for disabled user: " + user.getUsername(), logContext);
            throw new ForbiddenExceptionHandle(
                "Account disabled",
                "Your account has been disabled. Please contact administrator."
            );
        }

        // Tạo custom claims cho token
        Map<String, Object> claim = new HashMap<>();
        claim.put("role", user.getRole().name());
        claim.put("permissions", user.getPermissions().stream()
                .map(Permission::name)
                .collect(Collectors.toSet()));
        
        String accessToken = jwtService.generateToken(claim, user);
        String refreshToken = jwtService.generateRefreshToken(claim, user);

        return SecurityResponse.builder()
                .status(user.getStatus())
                .userId(user.getUserId())
                .username(user.getUsername())
                .age(user.getAge())
                .gender(user.getGender())
                .birth(user.getBirth())
                .role(user.getRole())
                .permissions(user.getPermissions())
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

        UserEntity user = userRepo.findByUsername(username)
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
                .map(Permission::name)
                .collect(Collectors.toSet()));
        
        String newAccessToken = jwtService.generateToken(accessClaims, user);

        return SecurityResponse.builder()
                .status(user.getStatus())
                .userId(user.getUserId())
                .username(user.getUsername())
                .age(user.getAge())
                .gender(user.getGender())
                .birth(user.getBirth())
                .role(user.getRole())
                .permissions(user.getPermissions())
                .accessToken(newAccessToken)
                .expires(formatExpirationTime(jwtConfig.getExpiration()))
                .refreshToken(refreshToken)
                .refExpires(formatExpirationTime(jwtService.extractExpiration(refreshToken).getTime()))
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
