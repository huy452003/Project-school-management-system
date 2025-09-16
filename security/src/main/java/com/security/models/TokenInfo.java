package com.security.models;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
// @JsonPropertyOrder({"username", "role", "permissions", "expiration", "issuedAt", "isExpired", "jti"})
public class TokenInfo {
    private String username;
    private String role;
    private List<String> permissions;
    private String expiration;
    private String issuedAt;
    private Boolean isExpired;
    private String jti;
}