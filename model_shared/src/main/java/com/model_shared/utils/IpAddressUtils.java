package com.model_shared.utils;

import jakarta.servlet.http.HttpServletRequest;

public class IpAddressUtils {
    
    public static String getClientIpAddress(HttpServletRequest request) {
        // X-Forwarded-For là Header chuẩn khi có proxy/load balancer, thường là IP thực của client
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            // nếu X-Forwarded-For không có thì lấy X-Real-IP
            // X-Real-IP là header cũ hơn, nhưng cũng thường được sử dụng, Nginx thường set header này
            ipAddress = request.getHeader("X-Real-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            // Proxy-Client-IP là header tương tự X-Forwarded-For, Apache proxy có thể set header này
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            // WL-Proxy-Client-IP là header tương tự X-Forwarded-For, WebLogic proxy có thể set header này
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            // trường hợp khi không có proxy headers thì lấy IP từ remote addr 
            ipAddress = request.getRemoteAddr();
        }
        
        // Tất cả các headers (X-Forwarded-For, X-Real-IP, Proxy-Client-IP, WL-Proxy-Client-IP)
        // đều có thể chứa nhiều IP (proxy chain), lấy IP đầu tiên (IP thực của client)
        if (ipAddress != null && ipAddress.contains(",")) {
            ipAddress = ipAddress.split(",")[0].trim();
        }
        
        // Nếu là localhost, trả về 127.0.0.1
        if (ipAddress == null || ipAddress.isEmpty() || "0:0:0:0:0:0:0:1".equals(ipAddress)) {
            ipAddress = "127.0.0.1";
        }
        
        return ipAddress;
    }
}

