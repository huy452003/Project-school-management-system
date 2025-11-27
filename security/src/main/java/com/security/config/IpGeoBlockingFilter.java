package com.security.config;

import com.handle_exceptions.IpBlockedExceptionHandle;
import com.logging.models.LogContext;
import com.logging.services.LoggingService;
import com.security.services.IpBlockingService;
import com.security.services.GeoBlockingService;
import com.model_shared.utils.IpAddressUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.beans.factory.annotation.Qualifier;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class IpGeoBlockingFilter extends OncePerRequestFilter {
    
    @Autowired
    private IpBlockingService ipBlockingService;
    
    @Autowired
    private GeoBlockingService geoBlockingService;
    
    @Autowired
    private LoggingService loggingService;
    
    @Autowired
    @Qualifier("handlerExceptionResolver")
    private HandlerExceptionResolver handlerExceptionResolver;
    
    // Các endpoint được bypass (bỏ qua) IP/Geo blocking hoàn toàn
    @Value("${security.ip.geo.bypass.endpoints:/auth/internal/**}")
    private String bypassEndpoints;
    
    // Các endpoint dùng policy linh hoạt (chỉ block manual blacklist, không block auto-blacklist)
    @Value("${security.ip.geo.relaxed.endpoints:/auth/register,/auth/login}")
    private String relaxedEndpoints;
    
    private LogContext getLogContext(String methodName) {
        return LogContext.builder()
            .module("security")
            .className(this.getClass().getSimpleName())
            .methodName(methodName)
            .build();
    }
    
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {
        
        LogContext logContext = getLogContext("doFilterInternal");
  
        String requestUri = request.getRequestURI();
        
        // Bypass hoàn toàn (không check gì cả) - cho internal APIs
        if (isBypassEndpoint(requestUri)) {
            loggingService.logDebug("Bypassing IP/Geo blocking completely for endpoint: " + requestUri, logContext);
            filterChain.doFilter(request, response);
            return;
        }
        
        // Lấy IP address từ request
        String ipAddress = IpAddressUtils.getClientIpAddress(request);
        loggingService.logDebug("Checking IP/Geo blocking for IP: " + ipAddress + ", URI: " + requestUri, logContext);
        
        try {
            // 1. Check IP blocking với policy phù hợp
            boolean isBlocked;
            if (isRelaxedEndpoint(requestUri)) {
                // Register/Login: Chỉ block manual blacklist, không block auto-blacklist (temporary)
                // Mục đích: Cho phép legitimate users đăng ký/đăng nhập ngay cả khi bị auto-block tạm thời
                isBlocked = ipBlockingService.isIpBlockedForAuth(ipAddress);
                if (isBlocked) {
                    loggingService.logWarn("IP " + ipAddress + " blocked for auth endpoint (manual blacklist only)", logContext);
                }
            } else {
                // Các endpoint khác: Block cả manual và auto-blacklist
                isBlocked = ipBlockingService.isIpBlocked(ipAddress);
            }
            
            if (isBlocked) {
                String reason = isRelaxedEndpoint(requestUri) 
                    ? ipBlockingService.getBlockReasonForAuth(ipAddress)
                    : ipBlockingService.getBlockReason(ipAddress);
                loggingService.logWarn("IP blocked: " + ipAddress + ", Reason: " + reason + ", URI: " + requestUri, logContext);
                
                IpBlockedExceptionHandle exception = new IpBlockedExceptionHandle(
                    "Access denied: IP address is blocked",
                    "Your IP address has been blocked. Please contact administrator.",
                    ipAddress,
                    reason
                );
                handlerExceptionResolver.resolveException(request, response, null, exception);
                return;
            }
            
            // 2. Check Geo blocking
            if (geoBlockingService.isIpBlockedByGeo(ipAddress)) {
                String reason = geoBlockingService.getBlockReason(ipAddress);
                loggingService.logWarn("IP blocked by Geo: " + ipAddress + ", Reason: " + reason, logContext);
                
                IpBlockedExceptionHandle exception = new IpBlockedExceptionHandle(
                    "Access denied: Your location is not allowed",
                    "Access from your country is not permitted. Please contact administrator.",
                    ipAddress,
                    reason
                );
                handlerExceptionResolver.resolveException(request, response, null, exception);
                return;
            }
            
            // Nếu pass tất cả checks, tiếp tục filter chain
            loggingService.logDebug("IP/Geo check passed for IP: " + ipAddress, logContext);
            filterChain.doFilter(request, response);
            
        } catch (IpBlockedExceptionHandle e) {
            // Re-throw để exception handler xử lý
            throw e;
        } catch (Exception e) {
            // Nếu có lỗi trong quá trình check, log và cho phép request (fail-open)
            loggingService.logError("Error in IP/Geo blocking filter", e, logContext);
            filterChain.doFilter(request, response);
        }
    }
    
    private boolean isBypassEndpoint(String uri) {
        if (bypassEndpoints == null || bypassEndpoints.isEmpty()) {
            return false;
        }
        
        List<String> bypassList = Arrays.asList(bypassEndpoints.split(","));
        for (String endpoint : bypassList) {
            String trimmed = endpoint.trim();
            if (uri.equals(trimmed) || uri.startsWith(trimmed)) {
                return true;
            }
        }
        return false;
    }
    
    private boolean isRelaxedEndpoint(String uri) {
        if (relaxedEndpoints == null || relaxedEndpoints.isEmpty()) {
            return false;
        }
        
        List<String> relaxedList = Arrays.asList(relaxedEndpoints.split(","));
        for (String endpoint : relaxedList) {
            String trimmed = endpoint.trim();
            if (uri.equals(trimmed) || uri.startsWith(trimmed)) {
                return true;
            }
        }
        return false;
    }
}

