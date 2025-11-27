package com.security.controllers;

import com.model_shared.models.Response;
import com.security.models.IpAddress;
import com.security.models.CountryCode;
import com.logging.models.LogContext;
import com.logging.services.LoggingService;
import com.security.services.IpBlockingService;
import com.security.services.GeoBlockingService;
import com.security.utils.SecurityUtils;
import com.model_shared.models.user.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    
    @Autowired
    private SecurityUtils securityUtils;
    
    private LogContext getLogContext(String methodName) {
        return LogContext.builder()
            .module("security")
            .className(this.getClass().getSimpleName())
            .methodName(methodName)
            .build();
    }
    
    // IP WHITELIST MANAGEMENT
    
    @PostMapping("/ip/whitelist")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Response<String>> addIpToWhitelist(
            @RequestBody IpAddress req,
            @RequestHeader(value = "Accept-Language", defaultValue = "en") String acceptLanguage) {
        Locale locale = Locale.forLanguageTag(acceptLanguage);
        LogContext logContext = getLogContext("addIpToWhitelist");
        
        UserDto currentUser = securityUtils.getCurrentUserDto();
        ipBlockingService.addToWhitelist(req.ipAddress());
        loggingService.logInfo((currentUser != null ? currentUser.getUsername() : "unknown") + 
            " added IP to whitelist: " + req.ipAddress(), logContext);
        
        Response<String> response = new Response<>(
            200,
            messageSource.getMessage("response.message.ipAddressAddedToWhitelistSuccess", null, locale),
            "IpGeoManagement",
            null,
            "IP " + req.ipAddress()
        );
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/ip/whitelist/{ipAddress}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Response<String>> removeIpFromWhitelist(
            @PathVariable String ipAddress,
            @RequestHeader(value = "Accept-Language", defaultValue = "en") String acceptLanguage) {
        Locale locale = Locale.forLanguageTag(acceptLanguage);
        LogContext logContext = getLogContext("removeIpFromWhitelist");
        
        UserDto currentUser = securityUtils.getCurrentUserDto();
        ipBlockingService.removeFromWhitelist(ipAddress);
        loggingService.logInfo((currentUser != null ? currentUser.getUsername() : "unknown") + 
            " removed IP from whitelist: " + ipAddress, logContext);
        
        Response<String> response = new Response<>(
            200,
            messageSource.getMessage("response.message.ipAddressRemovedFromWhitelistSuccess", null, locale),
            "IpGeoManagement",
            null,
            "IP " + ipAddress
        );
        return ResponseEntity.ok(response);
    }
    
    // IP BLACKLIST MANAGEMENT
     
    @PostMapping("/ip/blacklist")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Response<String>> addIpToBlacklist(
            @RequestBody IpAddress req,
            @RequestHeader(value = "Accept-Language", defaultValue = "en") String acceptLanguage) {
        Locale locale = Locale.forLanguageTag(acceptLanguage);
        LogContext logContext = getLogContext("addIpToBlacklist");
        
        UserDto currentUser = securityUtils.getCurrentUserDto();
        ipBlockingService.addToBlacklist(req.ipAddress());
        loggingService.logInfo((currentUser != null ? currentUser.getUsername() : "unknown") + 
            " added IP to blacklist: " + req.ipAddress(), logContext);
        
        Response<String> response = new Response<>(
            200,
            messageSource.getMessage("response.message.ipAddressAddedToBlacklistSuccess", null, locale),
            "IpGeoManagement",
            null,
            "IP " + req.ipAddress()
        );
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/ip/blacklist/{ipAddress}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Response<String>> removeIpFromBlacklist(
            @PathVariable String ipAddress,
            @RequestHeader(value = "Accept-Language", defaultValue = "en") String acceptLanguage) {
        Locale locale = Locale.forLanguageTag(acceptLanguage);
        LogContext logContext = getLogContext("removeIpFromBlacklist");
        
        UserDto currentUser = securityUtils.getCurrentUserDto();
        ipBlockingService.removeFromBlacklist(ipAddress);
        loggingService.logInfo((currentUser != null ? currentUser.getUsername() : "unknown") + 
            " removed IP from blacklist: " + ipAddress, logContext);
        
        Response<String> response = new Response<>(
            200,
            messageSource.getMessage("response.message.deleteSuccess", null, locale),
            "IpGeoManagement",
            null,
            "IP " + ipAddress
        );
        return ResponseEntity.ok(response);
    }
     
    // GEO WHITELIST MANAGEMENT

    @PostMapping("/geo/whitelist")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Response<String>> addCountryToWhitelist(
            @RequestBody CountryCode req,
            @RequestHeader(value = "Accept-Language", defaultValue = "en") String acceptLanguage) {
        Locale locale = Locale.forLanguageTag(acceptLanguage);
        LogContext logContext = getLogContext("addCountryToWhitelist");
        
        UserDto currentUser = securityUtils.getCurrentUserDto();
        geoBlockingService.addCountryToWhitelist(req.countryCode().toUpperCase());
        loggingService.logInfo((currentUser != null ? currentUser.getUsername() : "unknown") + 
            " added country to whitelist: " + req.countryCode().toUpperCase(), logContext);
        
        Response<String> response = new Response<>(
            200,
            messageSource.getMessage("response.message.ipAddressAddedToGeoWhitelistSuccess", null, locale),
            "IpGeoManagement",
            null,
            "Country " + req.countryCode().toUpperCase()
        );
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/geo/whitelist/{countryCode}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Response<String>> removeCountryFromWhitelist(
            @PathVariable String countryCode,
            @RequestHeader(value = "Accept-Language", defaultValue = "en") String acceptLanguage) {
        Locale locale = Locale.forLanguageTag(acceptLanguage);
        LogContext logContext = getLogContext("removeCountryFromWhitelist");
        
        UserDto currentUser = securityUtils.getCurrentUserDto();
        geoBlockingService.removeCountryFromWhitelist(countryCode.toUpperCase());
        loggingService.logInfo((currentUser != null ? currentUser.getUsername() : "unknown") + 
            " removed country from whitelist: " + countryCode.toUpperCase(), logContext);
        
        Response<String> response = new Response<>(
            200,
            messageSource.getMessage("response.message.ipAddressRemovedFromGeoWhitelistSuccess", null, locale),
            "IpGeoManagement",
            null,
            "Country " + countryCode.toUpperCase()
        );
        return ResponseEntity.ok(response);
    }

    // GEO BLACKLIST MANAGEMENT
    
    @PostMapping("/geo/blacklist")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Response<String>> addCountryToBlacklist(
            @RequestBody CountryCode req,
            @RequestHeader(value = "Accept-Language", defaultValue = "en") String acceptLanguage) {
        Locale locale = Locale.forLanguageTag(acceptLanguage);
        LogContext logContext = getLogContext("addCountryToBlacklist");
        
        UserDto currentUser = securityUtils.getCurrentUserDto();
        geoBlockingService.addCountryToBlacklist(req.countryCode().toUpperCase());
        loggingService.logInfo((currentUser != null ? currentUser.getUsername() : "unknown") + 
            " added country to blacklist: " + req.countryCode().toUpperCase(), logContext);
        
        Response<String> response = new Response<>(
            200,
            messageSource.getMessage("response.message.ipAddressAddedToGeoBlacklistSuccess", null, locale),
            "IpGeoManagement",
            null,
            "Country " + req.countryCode().toUpperCase()
        );
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/geo/blacklist/{countryCode}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Response<String>> removeCountryFromBlacklist(
            @PathVariable String countryCode,
            @RequestHeader(value = "Accept-Language", defaultValue = "en") String acceptLanguage) {
        Locale locale = Locale.forLanguageTag(acceptLanguage);
        LogContext logContext = getLogContext("removeCountryFromBlacklist");
        
        UserDto currentUser = securityUtils.getCurrentUserDto();
        geoBlockingService.removeCountryFromBlacklist(countryCode.toUpperCase());
        loggingService.logInfo((currentUser != null ? currentUser.getUsername() : "unknown") + 
            " removed country from blacklist: " + countryCode.toUpperCase(), logContext);
        
        Response<String> response = new Response<>(
            200,
            messageSource.getMessage("response.message.ipAddressRemovedFromGeoBlacklistSuccess", null, locale),
            "IpGeoManagement",
            null,
            "Country " + countryCode.toUpperCase()
        );
        return ResponseEntity.ok(response);
    }
    
    // IP STATUS MANAGEMENT

    @GetMapping("/ip/status/{ipAddress}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Response<Map<String, Object>>> getIpStatus(
            @PathVariable String ipAddress,
            @RequestHeader(value = "Accept-Language", defaultValue = "en") String acceptLanguage) {
        Locale locale = Locale.forLanguageTag(acceptLanguage);
        LogContext logContext = getLogContext("getIpStatus");
        
        UserDto currentUser = securityUtils.getCurrentUserDto();
        loggingService.logInfo((currentUser != null ? currentUser.getUsername() : "unknown") + 
            " checked IP status for: " + ipAddress, logContext);
        
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
}

