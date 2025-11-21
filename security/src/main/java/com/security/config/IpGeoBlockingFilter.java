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
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.beans.factory.annotation.Qualifier;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

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
    
    // Các endpoint được bypass (bỏ qua) IP/Geo blocking
    @Value("${security.ip.geo.bypass.endpoints:/auth/register,/auth/login,/public/health}")
    private String bypassEndpoints;
    
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
        
        if (isBypassEndpoint(requestUri)) {
            loggingService.logDebug("Bypassing IP/Geo blocking for endpoint: " + requestUri, logContext);
            filterChain.doFilter(request, response);
            return;
        }
        
        // Lấy IP address từ request
        String ipAddress = IpAddressUtils.getClientIpAddress(request);
        loggingService.logDebug("Checking IP/Geo blocking for IP: " + ipAddress + ", URI: " + requestUri, logContext);
        
        try {
            // 1. Check IP blocking trước
            if (ipBlockingService.isIpBlocked(ipAddress)) {
                String reason = ipBlockingService.getBlockReason(ipAddress);
                loggingService.logWarn("IP blocked: " + ipAddress + ", Reason: " + reason, logContext);
                
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
            if (uri.equals(endpoint.trim()) || uri.startsWith(endpoint.trim())) {
                return true;
            }
        }
        return false;
    }
}

