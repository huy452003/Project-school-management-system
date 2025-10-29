package com.security.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.time.LocalDate;
import com.model_shared.enums.Gender;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SecurityResponse {
    private Integer userId;
    private String userName;
    private Integer age;
    private Gender gender;
    private LocalDate birth;
    private String role;
    private List<String> permissions;
    private String accessToken;
    private String expires;
    private String refreshToken;
    private String refExpires;
}
