package com.security.services;

import com.logging.models.LogContext;
import com.logging.services.LoggingService;
import com.handle_exceptions.NotFoundExceptionHandle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Service để quản lý IP blocking/whitelisting
 * Lưu trữ IP whitelist/blacklist trong Redis và properties
 */
@Service
public class IpBlockingService {
    
    @Autowired
    @Qualifier("customStringRedisTemplate")
    private RedisTemplate<String, String> redisTemplate;
    
    @Autowired
    private LoggingService loggingService;
    
    // IP Whitelist từ properties (static, không thay đổi thường xuyên)
    @Value("${security.ip.whitelist:}")
    private String whitelistFromProperties;
    
    // IP Blacklist từ properties (static)
    @Value("${security.ip.blacklist:}")
    private String blacklistFromProperties;
    
    // Enable/Disable IP blocking
    @Value("${security.ip.blocking.enabled:true}")
    private boolean ipBlockingEnabled;
    
    // Mode: "whitelist" (chỉ cho phép IP trong whitelist) hoặc "blacklist" (chặn IP trong blacklist)
    @Value("${security.ip.blocking.mode:blacklist}")
    private String blockingMode;
    
    // Auto-blocking configuration
    @Value("${security.ip.auto-block.enabled:true}")
    private boolean autoBlockEnabled;
    
    @Value("${security.ip.auto-block.threshold:20}")
    private int autoBlockThreshold;
    
    @Value("${security.ip.auto-block.duration:24h}")
    private String autoBlockDuration;
    
    @Value("${security.ip.auto-block.escalate-threshold:50}")
    private int escalateThreshold;
    
    // Redis keys
    private static final String REDIS_IP_WHITELIST_PREFIX = "ip:whitelist:";
    private static final String REDIS_IP_BLACKLIST_PREFIX = "ip:blacklist:";
    private static final String REDIS_IP_AUTO_BLACKLIST_PREFIX = "ip:auto-blacklist:"; // Auto-blacklist (có TTL)
    private static final String REDIS_IP_BLOCKED_COUNT_PREFIX = "ip:blocked:count:";
    
    private LogContext getLogContext(String methodName) {
        return LogContext.builder()
            .module("security")
            .className(this.getClass().getSimpleName())
            .methodName(methodName)
            .build();
    }
    
    public boolean isIpBlocked(String ipAddress) {
        if (!ipBlockingEnabled) {
            return false; // Nếu disable IP blocking thì cho phép tất cả
        }
        
        LogContext logContext = getLogContext("isIpBlocked");
        
        if ("whitelist".equalsIgnoreCase(blockingMode)) {
            if (!isIpWhitelisted(ipAddress)) {
                loggingService.logWarn("IP " + ipAddress + " is not in whitelist (whitelist mode)", logContext);
                incrementBlockedCountAndAutoBlock(ipAddress);
                return true; // Block nếu không có trong whitelist
            }
            return false; // Cho phép nếu có trong whitelist
        }
        
        if ("blacklist".equalsIgnoreCase(blockingMode)) {
            if (isIpBlacklisted(ipAddress)) {
                loggingService.logWarn("IP " + ipAddress + " is in blacklist (blacklist mode)", logContext);
                incrementBlockedCountAndAutoBlock(ipAddress);
                return true; // Block nếu có trong blacklist
            }
            return false; // Cho phép nếu không có trong blacklist
        }
        
        // Default: không block
        return false;
    }
    
    public boolean isIpWhitelisted(String ipAddress) {
        Boolean inRedis = redisTemplate.hasKey(REDIS_IP_WHITELIST_PREFIX + ipAddress);
        if (Boolean.TRUE.equals(inRedis)) {
            return true;
        }
        
        if (whitelistFromProperties != null && !whitelistFromProperties.isEmpty()) {
            List<String> whitelist = Arrays.asList(whitelistFromProperties.split(","));
            for (String ip : whitelist) {
                if (ip.trim().equals(ipAddress) || matchesIpPattern(ip.trim(), ipAddress)) {
                    return true;
                }
            }
        }
        
        return false;
    }
    
    public boolean isIpBlacklisted(String ipAddress) {
        // Check nếu đã bị manual blacklist (bởi admin)
        Boolean inManualBlacklist = redisTemplate.hasKey(REDIS_IP_BLACKLIST_PREFIX + ipAddress);
        if (inManualBlacklist) {
            return true;
        }
        
        // Check nếu đã bị auto-blacklist (tự động bởi hệ thống)
        Boolean inAutoBlacklist = redisTemplate.hasKey(REDIS_IP_AUTO_BLACKLIST_PREFIX + ipAddress);
        if (inAutoBlacklist) {
            return true;
        }
        
        // Check nếu có trong blacklist từ properties
        if (blacklistFromProperties != null && !blacklistFromProperties.isEmpty()) {
            List<String> blacklist = Arrays.asList(blacklistFromProperties.split(","));
            for (String ip : blacklist) {
                if (ip.trim().equals(ipAddress) || matchesIpPattern(ip.trim(), ipAddress)) {
                    return true;
                }
            }
        }
        
        return false;
    }
    
    public boolean isIpBlockedForAuth(String ipAddress) {
        if (!ipBlockingEnabled) {
            return false;
        }
        
        LogContext logContext = getLogContext("isIpBlockedForAuth");
        
        if ("whitelist".equalsIgnoreCase(blockingMode)) {
            if (!isIpWhitelisted(ipAddress)) {
                loggingService.logWarn("IP " + ipAddress + " is not in whitelist (whitelist mode) for auth endpoint", logContext);
                return true; // Block nếu không có trong whitelist
            }
            return false;
        }
        
        if ("blacklist".equalsIgnoreCase(blockingMode)) {
            // Chỉ check manual blacklist (admin block) và properties blacklist
            // KHÔNG block auto-blacklist (temporary) để cho phép legitimate users
            Boolean inManualBlacklist = redisTemplate.hasKey(REDIS_IP_BLACKLIST_PREFIX + ipAddress);
            if (inManualBlacklist) {
                loggingService.logWarn("IP " + ipAddress + " is manually blacklisted (auth endpoint)", logContext);
                return true;
            }
            
            // Check properties blacklist
            if (blacklistFromProperties != null && !blacklistFromProperties.isEmpty()) {
                List<String> blacklist = Arrays.asList(blacklistFromProperties.split(","));
                for (String ip : blacklist) {
                    if (ip.trim().equals(ipAddress) || matchesIpPattern(ip.trim(), ipAddress)) {
                        loggingService.logWarn("IP " + ipAddress + " is in properties blacklist (auth endpoint)", logContext);
                        return true;
                    }
                }
            }
            
            // KHÔNG block auto-blacklist ở đây - cho phép register/login
            return false;
        }
        
        return false;
    }
    
    // Tăng số lần IP bị block và tự động blacklist nếu vượt threshold
    public void incrementBlockedCountAndAutoBlock(String ipAddress) {
        // Bỏ qua nếu IP đã trong whitelist
        if (isIpWhitelisted(ipAddress)) {
            return;
        }
        
        // 1. Tăng counter LUÔN (để track số lần vi phạm, dù đã bị blacklist)
        String key = REDIS_IP_BLOCKED_COUNT_PREFIX + ipAddress;
        Long count = redisTemplate.opsForValue().increment(key);
        redisTemplate.expire(key, 24, TimeUnit.HOURS); // Expire counter sau 24h
        
        LogContext logContext = getLogContext("incrementBlockedCountAndAutoBlock");
        
        // 2. Check escalation LUÔN (dù đã bị blacklist, vẫn cần alert admin)
        if (count != null && count >= escalateThreshold) {
            loggingService.logError("IP " + ipAddress + " has exceeded escalate threshold (" + escalateThreshold + "). " +
                "Current violations: " + count + ". Please review for permanent blacklist.", null, logContext);
        }
        
        // 3. Auto-blocking logic (chỉ khi chưa bị blacklist)
        if (autoBlockEnabled && count != null) {
            // Check nếu đã bị auto-blacklist rồi thì không cần làm gì
            Boolean alreadyAutoBlocked = redisTemplate.hasKey(REDIS_IP_AUTO_BLACKLIST_PREFIX + ipAddress);
            if (alreadyAutoBlocked) {
                return;
            }
            
            // Check nếu đã bị manual blacklist (bởi admin) rồi thì không cần auto-block
            Boolean alreadyManualBlocked = redisTemplate.hasKey(REDIS_IP_BLACKLIST_PREFIX + ipAddress);
            if (alreadyManualBlocked) {
                return;
            }
            
            // Auto-blacklist nếu vượt threshold
            if (count >= autoBlockThreshold) {
                addToAutoBlacklist(ipAddress);
                loggingService.logWarn("IP " + ipAddress + " auto-blacklisted after " + count + " violations (threshold: " + autoBlockThreshold + ")", logContext);
            }
        }
    }

    // Thêm vào auto-blacklist
    private void addToAutoBlacklist(String ipAddress) {
        long ttlSeconds = parseDurationToSeconds(autoBlockDuration);
        redisTemplate.opsForValue().set(
            REDIS_IP_AUTO_BLACKLIST_PREFIX + ipAddress, 
            "1", 
            ttlSeconds, 
            TimeUnit.SECONDS
        );
        LogContext logContext = getLogContext("addToAutoBlacklist");
        loggingService.logWarn("Auto-blacklisted IP: " + ipAddress + " for " + autoBlockDuration, logContext);
    }
    
    // chuyển đổi thời gian cấu hình ở properties thành seconds với các đơn vị s, m, h, d
    private long parseDurationToSeconds(String duration) {
        if (duration == null || duration.isEmpty()) {
            return 86400; // Default: 24 hours
        }
        
        duration = duration.trim().toLowerCase();
        try {
            if (duration.endsWith("s")) {
                return Long.parseLong(duration.substring(0, duration.length() - 1));
            } else if (duration.endsWith("m")) {
                return Long.parseLong(duration.substring(0, duration.length() - 1)) * 60;
            } else if (duration.endsWith("h")) {
                return Long.parseLong(duration.substring(0, duration.length() - 1)) * 3600;
            } else if (duration.endsWith("d")) {
                return Long.parseLong(duration.substring(0, duration.length() - 1)) * 86400;
            } else {
                return Long.parseLong(duration);
            }
        } catch (NumberFormatException e) {
            LogContext logContext = getLogContext("parseDurationToSeconds");
            loggingService.logError("Invalid duration format: " + duration + ". Using default 24h", e, logContext);
            return 86400; // Default: 24 hours
        }
    }
    
    // Kiểm tra IP có match pattern không (hỗ trợ CIDR và wildcard)
    private boolean matchesIpPattern(String pattern, String ipAddress) {
        // CIDR notation (e.g., 192.168.1.0/24)
        if (pattern.contains("/")) {
            return matchesCidr(pattern, ipAddress);
        }
        
        // Wildcard (e.g., 192.168.1.*)
        if (pattern.contains("*")) {
            String regex = pattern.replace(".", "\\.").replace("*", ".*");
            return ipAddress.matches(regex);
        }
        
        return false;
    }
    
    private boolean matchesCidr(String cidr, String ipAddress) {
        try {
            String[] parts = cidr.split("/");
            String networkIp = parts[0];
            int prefixLength = Integer.parseInt(parts[1]);
            
            long networkLong = ipToLong(networkIp);
            long ipLong = ipToLong(ipAddress);
            long mask = (0xFFFFFFFFL << (32 - prefixLength)) & 0xFFFFFFFFL;
            
            return (networkLong & mask) == (ipLong & mask);
        } catch (Exception e) {
            return false;
        }
    }
    
    private long ipToLong(String ipAddress) {
        String[] parts = ipAddress.split("\\.");
        long result = 0;
        for (int i = 0; i < 4; i++) {
            result = (result << 8) + Integer.parseInt(parts[i]);
        }
        return result;
    }
    
    public String getBlockReason(String ipAddress) {
        Boolean isAutoBlocked = redisTemplate.hasKey(REDIS_IP_AUTO_BLACKLIST_PREFIX + ipAddress);
        if (Boolean.TRUE.equals(isAutoBlocked)) {
            return "AUTO_BLACKLISTED";
        }
        
        if ("whitelist".equalsIgnoreCase(blockingMode)) {
            return "NOT_WHITELISTED";
        } else if ("blacklist".equalsIgnoreCase(blockingMode)) {
            return "BLACKLISTED";
        }
        return "UNKNOWN";
    }
    
    /**
     * Get block reason cho auth endpoints (chỉ manual blacklist)
     */
    public String getBlockReasonForAuth(String ipAddress) {
        Boolean isManualBlocked = redisTemplate.hasKey(REDIS_IP_BLACKLIST_PREFIX + ipAddress);
        if (Boolean.TRUE.equals(isManualBlocked)) {
            return "MANUAL_BLACKLISTED";
        }
        
        if ("whitelist".equalsIgnoreCase(blockingMode)) {
            return "NOT_WHITELISTED";
        } else if ("blacklist".equalsIgnoreCase(blockingMode)) {
            // Check properties blacklist
            if (blacklistFromProperties != null && !blacklistFromProperties.isEmpty()) {
                List<String> blacklist = Arrays.asList(blacklistFromProperties.split(","));
                for (String ip : blacklist) {
                    if (ip.trim().equals(ipAddress) || matchesIpPattern(ip.trim(), ipAddress)) {
                        return "PROPERTIES_BLACKLISTED";
                    }
                }
            }
        }
        return "UNKNOWN";
    }
    
    // Kiểm tra IP có bị auto-blacklist không 
    public boolean isIpAutoBlacklisted(String ipAddress) {
        Boolean inAutoBlacklist = redisTemplate.hasKey(REDIS_IP_AUTO_BLACKLIST_PREFIX + ipAddress);
        return Boolean.TRUE.equals(inAutoBlacklist);
    }
    
    // Kiểm tra IP có bị manual blacklist không
    public boolean isIpManualBlacklisted(String ipAddress) {
        Boolean inManualBlacklist = redisTemplate.hasKey(REDIS_IP_BLACKLIST_PREFIX + ipAddress);
        return Boolean.TRUE.equals(inManualBlacklist);
    }

    public void addToWhitelist(String ipAddress) {
        redisTemplate.opsForValue().set(REDIS_IP_WHITELIST_PREFIX + ipAddress, "1");
        LogContext logContext = getLogContext("addToWhitelist");
        loggingService.logInfo("Admin added IP to whitelist: " + ipAddress, logContext);
    }

    public void addToBlacklist(String ipAddress) {
        // Xóa auto-blacklist nếu có (thay bằng manual blacklist)
        redisTemplate.delete(REDIS_IP_AUTO_BLACKLIST_PREFIX + ipAddress);
        // Thêm vào manual blacklist (permanent)
        redisTemplate.opsForValue().set(REDIS_IP_BLACKLIST_PREFIX + ipAddress, "1");
        LogContext logContext = getLogContext("addToBlacklist");
        loggingService.logInfo("Admin added IP to manual blacklist (permanent): " + ipAddress, logContext);
    }

    public void removeFromWhitelist(String ipAddress) {
        LogContext logContext = getLogContext("removeFromWhitelist");
        
        // Check xem IP có trong whitelist (Redis) không trước khi xóa
        Boolean existsInRedis = redisTemplate.hasKey(REDIS_IP_WHITELIST_PREFIX + ipAddress);
        if (!existsInRedis) {
            loggingService.logWarn("Attempt to remove IP from whitelist that doesn't exist: " + ipAddress, logContext);
            throw new NotFoundExceptionHandle(
                "IP address not found in whitelist",
                List.of(ipAddress),
                "IpGeoManagement"
            );
        }
        
        redisTemplate.delete(REDIS_IP_WHITELIST_PREFIX + ipAddress);
        loggingService.logInfo("Admin removed IP from whitelist: " + ipAddress, logContext);
    }
    
    public void removeFromBlacklist(String ipAddress) {
        LogContext logContext = getLogContext("removeFromBlacklist");
        
        // Check xem IP có trong manual blacklist (Redis) không trước khi xóa
        // Lưu ý: Chỉ check manual blacklist, không check auto-blacklist vì auto-blacklist sẽ tự expire
        Boolean existsInManualBlacklist = redisTemplate.hasKey(REDIS_IP_BLACKLIST_PREFIX + ipAddress);
        if (!existsInManualBlacklist) {
            loggingService.logWarn("Attempt to remove IP from blacklist that doesn't exist: " + ipAddress, logContext);
            throw new NotFoundExceptionHandle(
                "IP address not found in blacklist",
                List.of(ipAddress),
                "IpGeoManagement"
            );
        }
        
        redisTemplate.delete(REDIS_IP_BLACKLIST_PREFIX + ipAddress);
        redisTemplate.delete(REDIS_IP_AUTO_BLACKLIST_PREFIX + ipAddress); // Xóa cả auto-blacklist nếu có
        loggingService.logInfo("Admin removed IP from blacklist: " + ipAddress, logContext);
    }

    public Long getBlockedCount(String ipAddress) {
        String count = redisTemplate.opsForValue().get(REDIS_IP_BLOCKED_COUNT_PREFIX + ipAddress);
        return count != null ? Long.parseLong(count) : 0L;
    }
}

