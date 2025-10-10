package com.security.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenInfo {
    private String username;
    private String role;
    private List<String> permissions;
    private String expiration;
    private String issuedAt;
    private Boolean isExpired;
    private String jti;
}