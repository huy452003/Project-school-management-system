package com.model_shared.enums;

public enum Status {
    PENDING,    // Đang chờ tạo profile/entity
    ENABLED,    // Đã tạo thành công và enabled
    DISABLED,   // Bị disable bởi admin
    FAILED      // Tạo profile/entity thất bại - cần rollback
}
