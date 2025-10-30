package com.security.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Set;
import java.time.LocalDate;
import com.model_shared.enums.Gender;
import com.model_shared.enums.Role;
import com.model_shared.enums.Permission;
import com.model_shared.enums.Status;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SecurityResponse {
    private Status status;
    private Integer userId;
    private String userName;
    private Integer age;
    private Gender gender;
    private LocalDate birth;
    private Role role;
    private Set<Permission> permissions;
    private String accessToken;
    private String expires;
    private String refreshToken;
    private String refExpires;
}
