package com.security_shared.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation để đánh dấu method cần authentication và authorization
 * Sử dụng với @RequiresAuthAspect để tự động validate
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiresAuth {
    /**
     * Danh sách roles được phép truy cập
     * Nếu empty thì không kiểm tra role
     */
    String[] roles() default {};
    
    /**
     * Danh sách permissions cần thiết
     * Nếu empty thì không kiểm tra permission
     */
    String[] permissions() default {};
    
    /**
     * Có yêu cầu authentication không
     * Mặc định là true
     */
    boolean requireAuth() default true;
}
