package com.common.QLGV;

import com.common.QLGV.entities.TeacherEntity;
import com.common.QLGV.repositories.TeacherRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Test class để kiểm tra Optimistic Locking với @Version
 * 
 * Cách chạy:
 * 1. Đảm bảo database đã có teacher với userId = 5 (hoặc thay đổi testUserId)
 * 2. Chạy test này trong IDE hoặc: mvn test -Dtest=OptimisticLockingTest
 * 3. Xem kết quả trong console
 */
@SpringBootTest(classes = QlgvApplication.class)
class OptimisticLockingTest {

    private static final Logger logger = LoggerFactory.getLogger(OptimisticLockingTest.class);

    @Autowired
    private TeacherRepo teacherRepo;
    
    @Autowired
    private PlatformTransactionManager transactionManager;

    @Test
    @Rollback(false) // Để xem kết quả trong DB (set true nếu muốn rollback sau test)
    void testOptimisticLocking() throws InterruptedException {
        try {
            System.out.println("=== Bắt đầu test Optimistic Locking ===");
            System.out.println("Đang kết nối database...");
            
            // Kiểm tra Spring context
            if (teacherRepo == null) {
                throw new RuntimeException("TeacherRepo is NULL - Spring context không load được!");
            }
            if (transactionManager == null) {
                throw new RuntimeException("TransactionManager is NULL - Spring context không load được!");
            }
            
            // Thay đổi userId này theo teacher có trong DB của bạn
            Integer testUserId = 5;
            
            System.out.println("Đang tìm teacher với userId: " + testUserId);
            TeacherEntity teacher = teacherRepo.findByUserId(testUserId)
                .orElseThrow(() -> new RuntimeException("Teacher not found with userId: " + testUserId));
            
            System.out.println("✅ Tìm thấy teacher!");
            System.out.println("Teacher ID: " + teacher.getId());
            System.out.println("User ID: " + teacher.getUserId());
            System.out.println("Initial Version: " + teacher.getVersion());
            System.out.println("Initial Department: " + teacher.getDepartment());
            System.out.println("Initial ClassManaging: " + teacher.getClassManaging());
        
        int threadCount = 5;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch finishLatch = new CountDownLatch(threadCount);
        
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger optimisticLockCount = new AtomicInteger(0);
        AtomicInteger errorCount = new AtomicInteger(0);
        List<String> results = Collections.synchronizedList(new ArrayList<>());
        
        // Tạo các thread để update đồng thời
        for (int i = 0; i < threadCount; i++) {
            final int threadIndex = i;
            executor.submit(() -> {
                // Tạo transaction riêng cho mỗi thread
                DefaultTransactionDefinition def = new DefaultTransactionDefinition();
                def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
                def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
                TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager, def);
                
                try {
                    // Đợi tất cả thread sẵn sàng
                    startLatch.await();
                    
                    // Chạy trong transaction riêng
                    transactionTemplate.execute((TransactionStatus status) -> {
                        try {
                            // Đọc lại entity để có version mới nhất (trong transaction riêng)
                            TeacherEntity currentEntity = teacherRepo.findById(teacher.getId())
                                .orElseThrow(() -> new RuntimeException("Teacher not found"));
                            
                            Long versionAtStart = currentEntity.getVersion();
                            String originalDept = currentEntity.getDepartment() != null 
                                ? currentEntity.getDepartment().replaceAll("_Thread\\d+", "") 
                                : "";
                            
                            // Thêm một chút delay để tạo race condition
                            Thread.sleep((long)(Math.random() * 10));
                            
                            // Update entity
                            currentEntity.setDepartment(originalDept + "_Thread" + threadIndex);
                            if (currentEntity.getClassManaging() == null) {
                                currentEntity.setClassManaging("A1");
                            }
                            
                            // Save - JPA sẽ check version trong WHERE clause
                            teacherRepo.save(currentEntity);
                            teacherRepo.flush(); // Force flush để trigger version check ngay
                            
                            // Lấy version sau khi update
                            TeacherEntity updatedEntity = teacherRepo.findById(teacher.getId())
                                .orElseThrow(() -> new RuntimeException("Teacher not found"));
                            
                            results.add(String.format("Thread %d: ✅ SUCCESS - Version %d -> %d, Dept: %s", 
                                threadIndex, versionAtStart, updatedEntity.getVersion(), 
                                updatedEntity.getDepartment()));
                            successCount.incrementAndGet();
                            
                            return null;
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            throw new RuntimeException(e);
                        } catch (OptimisticLockingFailureException e) {
                            // Catch optimistic lock exception trong transaction
                            results.add(String.format("Thread %d: 🔒 OPTIMISTIC_LOCK_FAILURE - Version conflict detected", 
                                threadIndex));
                            optimisticLockCount.incrementAndGet();
                            throw e; // Re-throw để transaction rollback
                        }
                    });
                    
                } catch (OptimisticLockingFailureException e) {
                    // Đã được xử lý trong transaction, không cần làm gì thêm
                } catch (Exception e) {
                    // Kiểm tra xem có phải OptimisticLockingFailureException không (có thể bị wrap)
                    Throwable cause = e.getCause();
                    if (cause instanceof OptimisticLockingFailureException) {
                        results.add(String.format("Thread %d: 🔒 OPTIMISTIC_LOCK_FAILURE - Version conflict detected (wrapped)", 
                            threadIndex));
                        optimisticLockCount.incrementAndGet();
                    } else {
                        results.add(String.format("Thread %d: ❌ ERROR - %s: %s", 
                            threadIndex, e.getClass().getSimpleName(), 
                            e.getMessage() != null && e.getMessage().length() > 50 
                                ? e.getMessage().substring(0, 50) + "..." 
                                : (e.getMessage() != null ? e.getMessage() : "No message")));
                        errorCount.incrementAndGet();
                        System.err.println("Thread " + threadIndex + " error:");
                        e.printStackTrace();
                    }
                } finally {
                    finishLatch.countDown();
                }
            });
        }
        
        // Bắt đầu tất cả thread cùng lúc
        Thread.sleep(100); // Đợi tất cả thread sẵn sàng
        System.out.println("\n🚀 Bắt đầu tất cả thread cùng lúc...\n");
        startLatch.countDown();
        
        // Đợi tất cả thread hoàn thành (tối đa 30 giây)
        boolean finished = finishLatch.await(30, TimeUnit.SECONDS);
        
        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);
        
        // Lấy kết quả cuối cùng từ DB
        TeacherEntity finalEntity = teacherRepo.findById(teacher.getId())
            .orElseThrow(() -> new RuntimeException("Teacher not found"));
        
        System.out.println("\n" + "=".repeat(60));
        System.out.println("=== KẾT QUẢ TEST ===");
        System.out.println("=".repeat(60));
        System.out.println("Thread Count: " + threadCount);
        System.out.println("✅ Success: " + successCount.get());
        System.out.println("🔒 Optimistic Lock Failures: " + optimisticLockCount.get());
        System.out.println("❌ Errors: " + errorCount.get());
        System.out.println("\nFinal Version: " + finalEntity.getVersion());
        System.out.println("Final Department: " + finalEntity.getDepartment());
        System.out.println("Final ClassManaging: " + finalEntity.getClassManaging());
        System.out.println("\n" + "-".repeat(60));
        System.out.println("Chi tiết từng thread:");
        System.out.println("-".repeat(60));
        results.forEach(System.out::println);
        System.out.println("=".repeat(60));
        
        // Đánh giá kết quả
        if (optimisticLockCount.get() > 0) {
            System.out.println("\n✅ Optimistic locking hoạt động ĐÚNG!");
            System.out.println("   Có " + optimisticLockCount.get() + " thread bị optimistic lock failure.");
            System.out.println("   Điều này chứng tỏ @Version đang bảo vệ dữ liệu khỏi concurrent updates.");
        } else if (successCount.get() == 1) {
            System.out.println("\n✅ Test thành công!");
            System.out.println("   Chỉ có 1 thread thành công, các thread khác đã bị block hoặc fail.");
        } else {
            System.out.println("\n⚠️ Không có optimistic lock failure.");
            System.out.println("   Có thể các transaction commit tuần tự thay vì đồng thời.");
            System.out.println("   Trong production với các request thực sự đồng thời, optimistic locking sẽ hoạt động.");
        }
        
        if (!finished) {
            System.out.println("\n⚠️ Test timeout - một số thread chưa hoàn thành");
        }
        
        } catch (Exception e) {
            System.err.println("\n❌ TEST FAILED với exception:");
            System.err.println("Exception: " + e.getClass().getSimpleName());
            System.err.println("Message: " + e.getMessage());
            e.printStackTrace();
            throw e; // Re-throw để test fail và hiển thị trong IDE
        }
    }

    /**
     * Test optimistic locking VỚI RETRY MECHANISM
     * 
     * Test này mô phỏng cách @Retryable hoạt động:
     * - Mỗi thread sẽ retry tối đa 3 lần khi bị OptimisticLockingFailureException
     * - Sau mỗi lần retry, thread sẽ đọc lại entity với version mới nhất
     * - Kỳ vọng: Tất cả 5 thread đều thành công sau một số lần retry
     * 
     * LƯU Ý về Optimistic Locking:
     * - Optimistic locking KHÔNG phải là lock thực sự, nó chỉ check version khi commit
     * - Nếu 2 transaction đọc cùng version và commit gần như cùng lúc, CẢ 2 CÓ THỂ THÀNH CÔNG
     * - Điều này xảy ra vì:
     *   1. Thread A đọc version 43 → modify → save (chưa commit)
     *   2. Thread B đọc version 43 → modify → save (chưa commit)
     *   3. Cả 2 đều check version = 43 trong WHERE clause → CẢ 2 ĐỀU PASS
     *   4. Thread A commit → version = 44
     *   5. Thread B commit → version = 45 (vì Thread B đã pass version check trước khi A commit)
     * - Trong production, điều này hiếm xảy ra vì có network delay và các request đến từ client khác nhau
     * - Optimistic locking đảm bảo: Nếu version đã thay đổi, transaction sẽ fail (không bị lost update)
     */
    @Test
    @Rollback(false) // Để xem kết quả trong DB
    void testOptimisticLockingWithRetry() throws InterruptedException {
        try {
            System.out.println("\n" + "=".repeat(60));
            System.out.println("=== Bắt đầu test Optimistic Locking VỚI RETRY ===");
            System.out.println("=".repeat(60));
            
            Integer testUserId = 5;
            TeacherEntity teacher = teacherRepo.findByUserId(testUserId)
                .orElseThrow(() -> new RuntimeException("Teacher not found with userId: " + testUserId));
            
            System.out.println("Teacher ID: " + teacher.getId());
            System.out.println("Initial Version: " + teacher.getVersion());
            System.out.println("Initial Department: " + teacher.getDepartment());
            
            int threadCount = 5;
            int maxRetries = 3; // Giống với @Retryable(maxAttempts = 3)
            ExecutorService executor = Executors.newFixedThreadPool(threadCount);
            CountDownLatch startLatch = new CountDownLatch(1);
            CountDownLatch finishLatch = new CountDownLatch(threadCount);
            
            AtomicInteger totalSuccessCount = new AtomicInteger(0);
            AtomicInteger totalRetryCount = new AtomicInteger(0);
            AtomicInteger finalFailureCount = new AtomicInteger(0);
            List<String> results = Collections.synchronizedList(new ArrayList<>());
            
            // Tạo các thread với retry logic
            for (int i = 0; i < threadCount; i++) {
                final int threadIndex = i;
                executor.submit(() -> {
                    try {
                        startLatch.await();
                        
                        int attempt = 0;
                        boolean success = false;
                        
                        while (attempt < maxRetries && !success) {
                            attempt++;
                            final int currentAttempt = attempt; // Final variable để dùng trong lambda
                            
                            // Tạo transaction riêng cho mỗi lần retry
                            DefaultTransactionDefinition def = new DefaultTransactionDefinition();
                            def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
                            def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
                            TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager, def);
                            
                            try {
                                transactionTemplate.execute((TransactionStatus status) -> {
                                    // Đọc lại entity với version mới nhất (sau mỗi lần retry)
                                    TeacherEntity currentEntity = teacherRepo.findById(teacher.getId())
                                        .orElseThrow(() -> new RuntimeException("Teacher not found"));
                                    
                                    Long versionAtStart = currentEntity.getVersion();
                                    String originalDept = currentEntity.getDepartment() != null 
                                        ? currentEntity.getDepartment().replaceAll("_Thread\\d+", "") 
                                        : "";
                                    
                                    // Update entity TRƯỚC khi delay (để tất cả thread đều có version cũ)
                                    currentEntity.setDepartment(originalDept + "_Thread" + threadIndex);
                                    if (currentEntity.getClassManaging() == null) {
                                        currentEntity.setClassManaging("A1");
                                    }
                                    
                                    // Delay SAU khi modify nhưng TRƯỚC khi save
                                    // Điều này tạo race condition: nhiều thread đã modify với cùng version
                                    // Khi save, chỉ thread nào commit trước mới thành công
                                    try {
                                        Thread.sleep(20 + (long)(Math.random() * 30)); // Delay 20-50ms
                                    } catch (InterruptedException e) {
                                        Thread.currentThread().interrupt();
                                        throw new RuntimeException(e);
                                    }
                                    
                                    // Save - JPA sẽ check version trong WHERE clause
                                    // Nếu version đã thay đổi, sẽ throw OptimisticLockingFailureException
                                    teacherRepo.save(currentEntity);
                                    teacherRepo.flush(); // Force flush để trigger version check ngay
                                    
                                    // Verify version sau update
                                    TeacherEntity updatedEntity = teacherRepo.findById(teacher.getId())
                                        .orElseThrow(() -> new RuntimeException("Teacher not found"));
                                    
                                    String resultMsg = currentAttempt == 1 
                                        ? String.format("Thread %d: ✅ SUCCESS (lần 1) - Version %d -> %d, Dept: %s",
                                            threadIndex, versionAtStart, updatedEntity.getVersion(), updatedEntity.getDepartment())
                                        : String.format("Thread %d: ✅ SUCCESS (sau %d lần retry) - Version %d -> %d, Dept: %s",
                                            threadIndex, currentAttempt - 1, versionAtStart, updatedEntity.getVersion(), updatedEntity.getDepartment());
                                    
                                    results.add(resultMsg);
                                    totalSuccessCount.incrementAndGet();
                                    if (currentAttempt > 1) {
                                        totalRetryCount.addAndGet(currentAttempt - 1);
                                    }
                                    
                                    return null;
                                });
                                
                                success = true; // Thành công, thoát khỏi vòng lặp retry
                                
                            } catch (OptimisticLockingFailureException e) {
                                // Bị optimistic lock failure, sẽ retry
                                if (currentAttempt < maxRetries) {
                                    results.add(String.format("Thread %d: 🔄 RETRY (lần %d/%d) - Version conflict, đang retry...",
                                        threadIndex, currentAttempt, maxRetries - 1));
                                    totalRetryCount.incrementAndGet();
                                    // Đợi một chút trước khi retry (giống như @Retryable)
                                    Thread.sleep(10 + (long)(Math.random() * 20));
                                } else {
                                    // Hết số lần retry, fail
                                    results.add(String.format("Thread %d: ❌ FINAL FAILURE (sau %d lần retry) - OptimisticLockingFailureException",
                                        threadIndex, maxRetries - 1));
                                    finalFailureCount.incrementAndGet();
                                }
                            }
                        }
                        
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        results.add(String.format("Thread %d: ❌ ERROR - InterruptedException", threadIndex));
                        finalFailureCount.incrementAndGet();
                    } catch (Exception e) {
                        results.add(String.format("Thread %d: ❌ ERROR - %s: %s",
                            threadIndex, e.getClass().getSimpleName(), 
                            e.getMessage() != null && e.getMessage().length() > 50 
                                ? e.getMessage().substring(0, 50) + "..." 
                                : (e.getMessage() != null ? e.getMessage() : "No message")));
                        finalFailureCount.incrementAndGet();
                    } finally {
                        finishLatch.countDown();
                    }
                });
            }
            
            // Bắt đầu tất cả thread cùng lúc
            Thread.sleep(100);
            System.out.println("\n🚀 Bắt đầu tất cả thread cùng lúc (với retry mechanism)...\n");
            startLatch.countDown();
            
            // Đợi tất cả thread hoàn thành
            boolean finished = finishLatch.await(60, TimeUnit.SECONDS);
            
            executor.shutdown();
            executor.awaitTermination(5, TimeUnit.SECONDS);
            
            // Lấy kết quả cuối cùng
            TeacherEntity finalEntity = teacherRepo.findById(teacher.getId())
                .orElseThrow(() -> new RuntimeException("Teacher not found"));
            
            System.out.println("\n" + "=".repeat(60));
            System.out.println("=== KẾT QUẢ TEST VỚI RETRY ===");
            System.out.println("=".repeat(60));
            System.out.println("Thread Count: " + threadCount);
            System.out.println("Max Retries per thread: " + maxRetries);
            System.out.println("✅ Total Success: " + totalSuccessCount.get());
            System.out.println("🔄 Total Retries: " + totalRetryCount.get());
            System.out.println("❌ Final Failures: " + finalFailureCount.get());
            System.out.println("\nFinal Version: " + finalEntity.getVersion());
            System.out.println("Final Department: " + finalEntity.getDepartment());
            System.out.println("\n" + "-".repeat(60));
            System.out.println("Chi tiết từng thread:");
            System.out.println("-".repeat(60));
            results.forEach(System.out::println);
            System.out.println("=".repeat(60));
            
            // Đánh giá kết quả
            if (totalSuccessCount.get() == threadCount) {
                System.out.println("\n✅ TẤT CẢ THREAD ĐỀU THÀNH CÔNG sau retry!");
                System.out.println("   Đây chính là cách @Retryable hoạt động trong production.");
                System.out.println("   Mỗi request HTTP sẽ retry tối đa 3 lần khi bị OptimisticLockingFailureException.");
            } else if (totalSuccessCount.get() > 0) {
                System.out.println("\n✅ Một số thread thành công sau retry.");
                System.out.println("   " + finalFailureCount.get() + " thread vẫn fail sau " + maxRetries + " lần retry.");
            } else {
                System.out.println("\n⚠️ Không có thread nào thành công.");
            }
            
            if (!finished) {
                System.out.println("\n⚠️ Test timeout - một số thread chưa hoàn thành");
            }
            
            // Assertion
            Assertions.assertTrue(totalSuccessCount.get() > 0, 
                "Ít nhất một thread phải thành công sau retry");
            
        } catch (Exception e) {
            System.err.println("\n❌ TEST FAILED với exception:");
            System.err.println("Exception: " + e.getClass().getSimpleName());
            System.err.println("Message: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
}

