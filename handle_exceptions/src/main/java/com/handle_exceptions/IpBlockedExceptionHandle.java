package com.handle_exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Exception cho trường hợp IP bị block
 * HTTP Status: 403 Forbidden
 */
@Data
@EqualsAndHashCode(callSuper=false)
@AllArgsConstructor
@NoArgsConstructor
public class IpBlockedExceptionHandle extends RuntimeException {
    private String message;
    private String details;
    private String ipAddress;
    private String reason; // "BLACKLISTED", "NOT_WHITELISTED", "GEO_BLOCKED"
    
    public IpBlockedExceptionHandle(String message, String ipAddress, String reason) {
        super(message);
        this.message = message;
        this.ipAddress = ipAddress;
        this.reason = reason;
    }
}

