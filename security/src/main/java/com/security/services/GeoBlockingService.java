package com.security.services;

import com.logging.models.LogContext;
import com.logging.services.LoggingService;
import com.handle_exceptions.NotFoundExceptionHandle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Service để quản lý Geo blocking (chặn theo quốc gia)
 * Sử dụng ip-api.com (miễn phí) để detect country từ IP
 */
@Service
public class GeoBlockingService {
    
    @Autowired
    @Qualifier("customStringRedisTemplate")
    private RedisTemplate<String, String> redisTemplate;
    
    @Autowired
    private LoggingService loggingService;
    
    @Autowired(required = false)
    private RestTemplate restTemplate;
    
    // Danh sách country codes bị block (từ properties)
    @Value("${security.geo.blocked.countries:}")
    private String blockedCountriesFromProperties;
    
    // Danh sách country codes được phép (whitelist mode)
    @Value("${security.geo.allowed.countries:}")
    private String allowedCountriesFromProperties;
    
    // Enable/Disable Geo blocking
    @Value("${security.geo.blocking.enabled:false}")
    private boolean geoBlockingEnabled;
    
    // Mode: "whitelist" (chỉ cho phép countries trong whitelist) hoặc "blacklist" (chặn countries trong blacklist)
    @Value("${security.geo.blocking.mode:blacklist}")
    private String blockingMode;
    
    // API endpoint để detect country (ip-api.com - miễn phí, không cần key)
    @Value("${security.geo.api.url:http://ip-api.com/json/{ip}?fields=status,countryCode,country}")
    private String geoApiUrl;
    
    // Cache country trong Redis để tránh gọi API nhiều lần
    private static final String REDIS_GEO_CACHE_PREFIX = "geo:cache:";
    private static final int GEO_CACHE_TTL_HOURS = 24; // Cache 24h
    
    // Redis keys cho country blocking
    private static final String REDIS_COUNTRY_BLACKLIST_PREFIX = "geo:blacklist:";
    private static final String REDIS_COUNTRY_WHITELIST_PREFIX = "geo:whitelist:";
    
    private LogContext getLogContext(String methodName) {
        return LogContext.builder()
            .module("security")
            .className(this.getClass().getSimpleName())
            .methodName(methodName)
            .build();
    }
    
    public boolean isIpBlockedByGeo(String ipAddress) {
        if (!geoBlockingEnabled) {
            return false; // Nếu disable Geo blocking thì cho phép tất cả
        }
        
        LogContext logContext = getLogContext("isIpBlockedByGeo");
        
        try {
            // Lấy country code từ IP
            String countryCode = getCountryCode(ipAddress);
            
            if (countryCode == null || countryCode.isEmpty()) {
                // Nếu không detect được country, có thể cho phép hoặc block tùy config
                loggingService.logWarn("Cannot detect country for IP: " + ipAddress, logContext);
                return false; // Default: cho phép nếu không detect được
            }
            
            // 1. Check whitelist mode: chỉ cho phép countries trong whitelist
            if ("whitelist".equalsIgnoreCase(blockingMode)) {
                if (!isCountryAllowed(countryCode)) {
                    loggingService.logWarn("IP " + ipAddress + " from country " + countryCode + 
                        " is not in whitelist (whitelist mode)", logContext);
                    return true; // Block nếu không có trong whitelist
                }
                return false; // Cho phép nếu có trong whitelist
            }
            
            // 2. Check blacklist mode: chặn countries trong blacklist
            if ("blacklist".equalsIgnoreCase(blockingMode)) {
                if (isCountryBlocked(countryCode)) {
                    loggingService.logWarn("IP " + ipAddress + " from country " + countryCode + 
                        " is in blacklist (blacklist mode)", logContext);
                    return true; // Block nếu có trong blacklist
                }
                return false; // Cho phép nếu không có trong blacklist
            }
            
        } catch (Exception e) {
            loggingService.logError("Error checking geo blocking for IP: " + ipAddress, e, logContext);
            // Nếu có lỗi, cho phép request (fail-open) để tránh block nhầm
            return false;
        }
        
        return false;
    }
    
    public String getCountryCode(String ipAddress) {
        if (ipAddress == null || ipAddress.isEmpty()) {
            return null;
        }
        
        String cachedCountry = redisTemplate.opsForValue().get(REDIS_GEO_CACHE_PREFIX + ipAddress);
        if (cachedCountry != null && !cachedCountry.isEmpty()) {
            return cachedCountry;
        }
        
        try {
            if (restTemplate == null) {
                restTemplate = new RestTemplate();
            }
            
            String url = geoApiUrl.replace("{ip}", ipAddress);
            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            
            if (response != null && "success".equals(response.get("status"))) {
                String countryCode = (String) response.get("countryCode");
                
                // Cache kết quả
                if (countryCode != null && !countryCode.isEmpty()) {
                    redisTemplate.opsForValue().set(
                        REDIS_GEO_CACHE_PREFIX + ipAddress, 
                        countryCode, 
                        GEO_CACHE_TTL_HOURS, 
                        TimeUnit.HOURS
                    );
                    return countryCode;
                }
            }
        } catch (Exception e) {
            LogContext logContext = getLogContext("getCountryCode");
            loggingService.logError("Error calling geo API for IP: " + ipAddress, e, logContext);
        }
        
        return null;
    }
    
    public boolean isCountryBlocked(String countryCode) {
        if (countryCode == null || countryCode.isEmpty()) {
            return false;
        }
        
        Boolean inRedis = redisTemplate.hasKey(REDIS_COUNTRY_BLACKLIST_PREFIX + countryCode.toUpperCase());
        if (Boolean.TRUE.equals(inRedis)) {
            return true;
        }
        
        if (blockedCountriesFromProperties != null && !blockedCountriesFromProperties.isEmpty()) {
            List<String> blockedCountries = Arrays.asList(blockedCountriesFromProperties.split(","));
            for (String country : blockedCountries) {
                if (country.trim().equalsIgnoreCase(countryCode)) {
                    return true;
                }
            }
        }
        
        return false;
    }
    
    public boolean isCountryAllowed(String countryCode) {
        if (countryCode == null || countryCode.isEmpty()) {
            return false;
        }
        
        Boolean inRedis = redisTemplate.hasKey(REDIS_COUNTRY_WHITELIST_PREFIX + countryCode.toUpperCase());
        if (Boolean.TRUE.equals(inRedis)) {
            return true;
        }
        
        if (allowedCountriesFromProperties != null && !allowedCountriesFromProperties.isEmpty()) {
            List<String> allowedCountries = Arrays.asList(allowedCountriesFromProperties.split(","));
            for (String country : allowedCountries) {
                if (country.trim().equalsIgnoreCase(countryCode)) {
                    return true;
                }
            }
        }
        
        return false;
    }
    
    public void addCountryToBlacklist(String countryCode) {
        redisTemplate.opsForValue().set(REDIS_COUNTRY_BLACKLIST_PREFIX + countryCode.toUpperCase(), "1");
        LogContext logContext = getLogContext("addCountryToBlacklist");
        loggingService.logInfo("Admin added country to blacklist: " + countryCode, logContext);
    }
    
    public void addCountryToWhitelist(String countryCode) {
        redisTemplate.opsForValue().set(REDIS_COUNTRY_WHITELIST_PREFIX + countryCode.toUpperCase(), "1");
        LogContext logContext = getLogContext("addCountryToWhitelist");
        loggingService.logInfo("Admin added country to whitelist: " + countryCode, logContext);
    }
    
    public void removeCountryFromBlacklist(String countryCode) {
        LogContext logContext = getLogContext("removeCountryFromBlacklist");
        
        // Check xem country có trong blacklist (Redis) không trước khi xóa
        Boolean existsInRedis = redisTemplate.hasKey(REDIS_COUNTRY_BLACKLIST_PREFIX + countryCode.toUpperCase());
        if (!existsInRedis) {
            loggingService.logWarn("Attempt to remove country from blacklist that doesn't exist: " + countryCode, logContext);
            throw new NotFoundExceptionHandle(
                "Country not found in blacklist",
                List.of(countryCode.toUpperCase()),
                "IpGeoManagement"
            );
        }
        
        redisTemplate.delete(REDIS_COUNTRY_BLACKLIST_PREFIX + countryCode.toUpperCase());
        loggingService.logInfo("Admin removed country from blacklist: " + countryCode, logContext);
    }
    
    public void removeCountryFromWhitelist(String countryCode) {
        LogContext logContext = getLogContext("removeCountryFromWhitelist");
        
        // Check xem country có trong whitelist (Redis) không trước khi xóa
        Boolean existsInRedis = redisTemplate.hasKey(REDIS_COUNTRY_WHITELIST_PREFIX + countryCode.toUpperCase());
        if (!existsInRedis) {
            loggingService.logWarn("Attempt to remove country from whitelist that doesn't exist: " + countryCode, logContext);
            throw new NotFoundExceptionHandle(
                "Country not found in whitelist",
                List.of(countryCode.toUpperCase()),
                "IpGeoManagement"
            );
        }
        
        redisTemplate.delete(REDIS_COUNTRY_WHITELIST_PREFIX + countryCode.toUpperCase());
        loggingService.logInfo("Admin removed country from whitelist: " + countryCode, logContext);
    }
    
    public String getBlockReason(String ipAddress) {
        String countryCode = getCountryCode(ipAddress);
        if (countryCode != null) {
            if ("whitelist".equalsIgnoreCase(blockingMode)) {
                return "GEO_BLOCKED: Country " + countryCode + " not in whitelist";
            } else if ("blacklist".equalsIgnoreCase(blockingMode)) {
                return "GEO_BLOCKED: Country " + countryCode + " is blocked";
            }
        }
        return "GEO_BLOCKED";
    }
}

