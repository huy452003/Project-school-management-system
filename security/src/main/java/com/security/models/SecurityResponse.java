package com.security.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SecurityResponse {
    private String userName;
    private String role;
    private List<String> permissions;
    private String accessToken;
    private String expires;
    private String refreshToken;
    private String refExpires;
}
