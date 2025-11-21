package com.security.controllers;

import com.model_shared.models.Response;
import com.logging.models.LogContext;
import com.logging.services.LoggingService;
import com.security.services.IpBlockingService;
import com.security.services.GeoBlockingService;
import com.security_shared.annotations.RequiresAuth;
import com.security_shared.annotations.CurrentUser;
import com.model_shared.models.user.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@RestController
@RequestMapping("/admin/ip-geo")
public class IpGeoManagementController {
    
    @Autowired
    private IpBlockingService ipBlockingService;
    
    @Autowired
    private GeoBlockingService geoBlockingService;
    
    @Autowired
    private LoggingService loggingService;
    
    @Autowired
    private ReloadableResourceBundleMessageSource messageSource;
    
    private LogContext getLogContext(String methodName) {
        return LogContext.builder()
            .module("security")
            .className(this.getClass().getSimpleName())
            .methodName(methodName)
            .build();
    }
    
    // ========================================
    // IP WHITELIST MANAGEMENT
    // ========================================
    
    @PostMapping("/ip/whitelist")
    @RequiresAuth(roles = {"ADMIN"})
    public ResponseEntity<Response<String>> addIpToWhitelist(
            @RequestBody Map<String, String> request,
            @RequestHeader(value = "Accept-Language", defaultValue = "en") String acceptLanguage,
            @CurrentUser UserDto currentUser) {
        Locale locale = Locale.forLanguageTag(acceptLanguage);
        LogContext logContext = getLogContext("addIpToWhitelist");
        
        String ipAddress = request.get("ipAddress");
        if (ipAddress == null || ipAddress.isEmpty()) {
            Response<String> response = new Response<>(
                400,
                "IP address is required",
                "IpGeoManagement",
                null,
                null
            );
            return ResponseEntity.badRequest().body(response);
        }
        
        ipBlockingService.addToWhitelist(ipAddress);
        loggingService.logInfo("Admin " + currentUser.getUsername() + " added IP to whitelist: " + ipAddress, logContext);
        
        Response<String> response = new Response<>(
            200,
            messageSource.getMessage("response.message.updateSuccess", null, locale),
            "IpGeoManagement",
            null,
            "IP " + ipAddress + " added to whitelist successfully"
        );
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/ip/whitelist/{ipAddress}")
    @RequiresAuth(roles = {"ADMIN"})
    public ResponseEntity<Response<String>> removeIpFromWhitelist(
            @PathVariable String ipAddress,
            @RequestHeader(value = "Accept-Language", defaultValue = "en") String acceptLanguage,
            @CurrentUser UserDto currentUser) {
        Locale locale = Locale.forLanguageTag(acceptLanguage);
        LogContext logContext = getLogContext("removeIpFromWhitelist");
        
        ipBlockingService.removeFromWhitelist(ipAddress);
        loggingService.logInfo("Admin " + currentUser.getUsername() + " removed IP from whitelist: " + ipAddress, logContext);
        
        Response<String> response = new Response<>(
            200,
            messageSource.getMessage("response.message.deleteSuccess", null, locale),
            "IpGeoManagement",
            null,
            "IP " + ipAddress + " removed from whitelist successfully"
        );
        return ResponseEntity.ok(response);
    }
    
    // ========================================
    // IP BLACKLIST MANAGEMENT
    // ========================================
    
    @PostMapping("/ip/blacklist")
    @RequiresAuth(roles = {"ADMIN"})
    public ResponseEntity<Response<String>> addIpToBlacklist(
            @RequestBody Map<String, String> request,
            @RequestHeader(value = "Accept-Language", defaultValue = "en") String acceptLanguage,
            @CurrentUser UserDto currentUser) {
        Locale locale = Locale.forLanguageTag(acceptLanguage);
        LogContext logContext = getLogContext("addIpToBlacklist");
        
        String ipAddress = request.get("ipAddress");
        if (ipAddress == null || ipAddress.isEmpty()) {
            Response<String> response = new Response<>(
                400,
                "IP address is required",
                "IpGeoManagement",
                null,
                null
            );
            return ResponseEntity.badRequest().body(response);
        }
        
        ipBlockingService.addToBlacklist(ipAddress);
        loggingService.logInfo("Admin " + currentUser.getUsername() + " added IP to blacklist: " + ipAddress, logContext);
        
        Response<String> response = new Response<>(
            200,
            messageSource.getMessage("response.message.updateSuccess", null, locale),
            "IpGeoManagement",
            null,
            "IP " + ipAddress + " added to blacklist successfully"
        );
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/ip/blacklist/{ipAddress}")
    @RequiresAuth(roles = {"ADMIN"})
    public ResponseEntity<Response<String>> removeIpFromBlacklist(
            @PathVariable String ipAddress,
            @RequestHeader(value = "Accept-Language", defaultValue = "en") String acceptLanguage,
            @CurrentUser UserDto currentUser) {
        Locale locale = Locale.forLanguageTag(acceptLanguage);
        LogContext logContext = getLogContext("removeIpFromBlacklist");
        
        ipBlockingService.removeFromBlacklist(ipAddress);
        loggingService.logInfo("Admin " + currentUser.getUsername() + " removed IP from blacklist: " + ipAddress, logContext);
        
        Response<String> response = new Response<>(
            200,
            messageSource.getMessage("response.message.deleteSuccess", null, locale),
            "IpGeoManagement",
            null,
            "IP " + ipAddress + " removed from blacklist successfully"
        );
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/ip/status/{ipAddress}")
    @RequiresAuth(roles = {"ADMIN"})
    public ResponseEntity<Response<Map<String, Object>>> getIpStatus(
            @PathVariable String ipAddress,
            @RequestHeader(value = "Accept-Language", defaultValue = "en") String acceptLanguage,
            @CurrentUser UserDto currentUser) {
        Locale locale = Locale.forLanguageTag(acceptLanguage);
        
        Long blockedCount = ipBlockingService.getBlockedCount(ipAddress);
        boolean isAutoBlacklisted = ipBlockingService.isIpAutoBlacklisted(ipAddress);
        boolean isManualBlacklisted = ipBlockingService.isIpManualBlacklisted(ipAddress);
        boolean isWhitelisted = ipBlockingService.isIpWhitelisted(ipAddress);
        String blockReason = ipBlockingService.getBlockReason(ipAddress);
        
        Map<String, Object> data = new HashMap<>();
        data.put("ipAddress", ipAddress);
        data.put("blockedCount", blockedCount);
        data.put("isAutoBlacklisted", isAutoBlacklisted);
        data.put("isManualBlacklisted", isManualBlacklisted);
        data.put("isWhitelisted", isWhitelisted);
        data.put("blockReason", blockReason);
        data.put("status", isWhitelisted ? "WHITELISTED" : 
                          (isManualBlacklisted ? "MANUAL_BLACKLISTED" : 
                           (isAutoBlacklisted ? "AUTO_BLACKLISTED" : "ALLOWED")));
        
        Response<Map<String, Object>> response = new Response<>(
            200,
            messageSource.getMessage("response.message.getSuccess", null, locale),
            "IpGeoManagement",
            null,
            data
        );
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/ip/blocked-count/{ipAddress}")
    @RequiresAuth(roles = {"ADMIN"})
    public ResponseEntity<Response<Map<String, Object>>> getIpBlockedCount(
            @PathVariable String ipAddress,
            @RequestHeader(value = "Accept-Language", defaultValue = "en") String acceptLanguage,
            @CurrentUser UserDto currentUser) {
        // Redirect to getIpStatus
        return getIpStatus(ipAddress, acceptLanguage, currentUser);
    }
    
    // ========================================
    // GEO BLOCKING MANAGEMENT
    // ========================================
    
    @PostMapping("/geo/blacklist")
    @RequiresAuth(roles = {"ADMIN"})
    public ResponseEntity<Response<String>> addCountryToBlacklist(
            @RequestBody Map<String, String> request,
            @RequestHeader(value = "Accept-Language", defaultValue = "en") String acceptLanguage,
            @CurrentUser UserDto currentUser) {
        Locale locale = Locale.forLanguageTag(acceptLanguage);
        LogContext logContext = getLogContext("addCountryToBlacklist");
        
        String countryCode = request.get("countryCode");
        if (countryCode == null || countryCode.isEmpty()) {
            Response<String> response = new Response<>(
                400,
                "Country code is required (ISO 3166-1 alpha-2, e.g., CN, US, VN)",
                "IpGeoManagement",
                null,
                null
            );
            return ResponseEntity.badRequest().body(response);
        }
        
        geoBlockingService.addCountryToBlacklist(countryCode.toUpperCase());
        loggingService.logInfo("Admin " + currentUser.getUsername() + " added country to blacklist: " + countryCode, logContext);
        
        Response<String> response = new Response<>(
            200,
            messageSource.getMessage("response.message.updateSuccess", null, locale),
            "IpGeoManagement",
            null,
            "Country " + countryCode.toUpperCase() + " added to blacklist successfully"
        );
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/geo/blacklist/{countryCode}")
    @RequiresAuth(roles = {"ADMIN"})
    public ResponseEntity<Response<String>> removeCountryFromBlacklist(
            @PathVariable String countryCode,
            @RequestHeader(value = "Accept-Language", defaultValue = "en") String acceptLanguage,
            @CurrentUser UserDto currentUser) {
        Locale locale = Locale.forLanguageTag(acceptLanguage);
        LogContext logContext = getLogContext("removeCountryFromBlacklist");
        
        geoBlockingService.removeCountryFromBlacklist(countryCode.toUpperCase());
        loggingService.logInfo("Admin " + currentUser.getUsername() + " removed country from blacklist: " + countryCode, logContext);
        
        Response<String> response = new Response<>(
            200,
            messageSource.getMessage("response.message.deleteSuccess", null, locale),
            "IpGeoManagement",
            null,
            "Country " + countryCode.toUpperCase() + " removed from blacklist successfully"
        );
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/geo/whitelist")
    @RequiresAuth(roles = {"ADMIN"})
    public ResponseEntity<Response<String>> addCountryToWhitelist(
            @RequestBody Map<String, String> request,
            @RequestHeader(value = "Accept-Language", defaultValue = "en") String acceptLanguage,
            @CurrentUser UserDto currentUser) {
        Locale locale = Locale.forLanguageTag(acceptLanguage);
        LogContext logContext = getLogContext("addCountryToWhitelist");
        
        String countryCode = request.get("countryCode");
        if (countryCode == null || countryCode.isEmpty()) {
            Response<String> response = new Response<>(
                400,
                "Country code is required (ISO 3166-1 alpha-2, e.g., CN, US, VN)",
                "IpGeoManagement",
                null,
                null
            );
            return ResponseEntity.badRequest().body(response);
        }
        
        geoBlockingService.addCountryToWhitelist(countryCode.toUpperCase());
        loggingService.logInfo("Admin " + currentUser.getUsername() + " added country to whitelist: " + countryCode, logContext);
        
        Response<String> response = new Response<>(
            200,
            messageSource.getMessage("response.message.updateSuccess", null, locale),
            "IpGeoManagement",
            null,
            "Country " + countryCode.toUpperCase() + " added to whitelist successfully"
        );
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/geo/whitelist/{countryCode}")
    @RequiresAuth(roles = {"ADMIN"})
    public ResponseEntity<Response<String>> removeCountryFromWhitelist(
            @PathVariable String countryCode,
            @RequestHeader(value = "Accept-Language", defaultValue = "en") String acceptLanguage,
            @CurrentUser UserDto currentUser) {
        Locale locale = Locale.forLanguageTag(acceptLanguage);
        LogContext logContext = getLogContext("removeCountryFromWhitelist");
        
        geoBlockingService.removeCountryFromWhitelist(countryCode.toUpperCase());
        loggingService.logInfo("Admin " + currentUser.getUsername() + " removed country from whitelist: " + countryCode, logContext);
        
        Response<String> response = new Response<>(
            200,
            messageSource.getMessage("response.message.deleteSuccess", null, locale),
            "IpGeoManagement",
            null,
            "Country " + countryCode.toUpperCase() + " removed from whitelist successfully"
        );
        return ResponseEntity.ok(response);
    }
}

