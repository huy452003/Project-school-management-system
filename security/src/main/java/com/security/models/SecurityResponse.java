package com.security.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SecurityResponse {
    private String userName;
    private String role;
    private String accessToken;
    private String expires;
    private String refreshToken;
    private String refExpires;
}
