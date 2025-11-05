package com.security.services;

import com.security.repositories.UserRepo;
import com.security.entities.UserEntity;
import com.model_shared.enums.Status;
import com.logging.services.LoggingService;
import com.logging.models.LogContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PendingUserTimeoutService {

    @Autowired
    private UserRepo userRepo;
    
    @Autowired
    private LoggingService loggingService;
    
    @Value("${user.registration.pending-timeout:300000}")
    private Long pendingTimeout; // Default: 5 phút
    
    private LogContext getLogContext(String methodName) {
        return LogContext.builder()
                .module("security")
                .className(this.getClass().getSimpleName())
                .methodName(methodName)
                .build();
    }
    
    /**
     * Scheduled job chạy định kỳ để detect và xử lý các user PENDING timeout
     * Cron expression từ config: mặc định mỗi 2 phút
     * 
     * Lưu ý: Không retry gửi lại event vì:
     * - Event đã được gửi vào Kafka topic và vẫn nằm đó
     * - Khi consumer online lại sẽ tự động consume
     * - Kafka đã có retry mechanism riêng (3 lần, mỗi lần 1 giây)
     * 
     * Job này chỉ set status = FAILED cho user PENDING quá lâu
     * để user có thể register lại
     */
    @Scheduled(cron = "${user.registration.cleanup-cron:0 */2 * * * ?}")
    @Transactional
    public void checkPendingUserTimeout() {
        LogContext logContext = getLogContext("checkPendingUserTimeout");
        loggingService.logInfo("Starting scheduled check for PENDING user timeout...", logContext);
        
        try {
            // Tìm tất cả user có status = PENDING
            List<UserEntity> pendingUsers = userRepo.findByStatus(Status.PENDING);
            
            if (pendingUsers.isEmpty()) {
                loggingService.logInfo("No PENDING users found", logContext);
                return;
            }
            
            loggingService.logInfo("Found " + pendingUsers.size() + " PENDING users to check", logContext);
            
            // Tính toán userId threshold dựa trên timeout
            // Giả sử userId tăng tuyến tính theo thời gian
            // Nếu user mới nhất có userId = 100, và timeout là 5 phút
            // Thì user có userId <= (100 - số user được tạo trong 5 phút) là timeout
            
            Integer latestUserId = userRepo.findMaxUserId().orElse(0);
            
            int failedCount = 0;
            
            for (UserEntity user : pendingUsers) {
                try {
                    // Kiểm tra xem user đã PENDING quá lâu chưa
                    // Logic đơn giản: nếu userId cách userId mới nhất > threshold thì timeout
                    // Threshold = số user có thể tạo trong khoảng thời gian timeout
                    // Ví dụ: trong 5 phút có thể tạo được ~10 user, nếu cách > 10 ID thì timeout
                    
                    int userIdGap = latestUserId - user.getUserId();
                    
                    // Heuristic: giả sử 1 user/30s = 10 users trong 5 phút
                    int timeoutThreshold = (int) (pendingTimeout / 30000);
                    
                    if (userIdGap > timeoutThreshold) {
                        loggingService.logWarn(
                            "User " + user.getUsername() + " (ID: " + user.getUserId() + 
                            ") has been PENDING for too long. Gap: " + userIdGap + 
                            ", Threshold: " + timeoutThreshold + ". Setting status to FAILED.", 
                            logContext
                        );
                        
                        // Set status = FAILED để user có thể register lại
                        user.setStatus(Status.FAILED);
                        userRepo.save(user);
                        failedCount++;
                        
                        loggingService.logWarn(
                            "Set user " + user.getUsername() + " (ID: " + user.getUserId() + 
                            ") to FAILED status after timeout", 
                            logContext
                        );
                    }
                } catch (Exception e) {
                    loggingService.logError(
                        "Error processing PENDING user: " + user.getUsername(), 
                        e, 
                        logContext
                    );
                }
            }
            
            if (failedCount > 0) {
                loggingService.logWarn(
                    "Finished checking PENDING users. Set " + failedCount + 
                    " user(s) to FAILED status due to timeout", 
                    logContext
                );
            } else {
                loggingService.logInfo("All PENDING users are within timeout threshold", logContext);
            }
            
        } catch (Exception e) {
            loggingService.logError("Error in checkPendingUserTimeout", e, logContext);
        }
    }
    
}

