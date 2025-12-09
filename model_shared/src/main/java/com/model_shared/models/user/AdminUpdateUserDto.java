package com.model_shared.models.user;

import com.model_shared.enums.Permission;
import com.model_shared.enums.Role;
import com.model_shared.enums.Status;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminUpdateUserDto {
    @NotNull(message = "{validate.user.userId.notNull}")
    private Integer userId;

    @NotNull(message = "{validate.user.role.notNull}")
    private Role role;

    private Set<Permission> permissions;
    
    // Optional fields - chỉ ADMIN mới được update
    private String username; // Optional - chỉ validate nếu có giá trị

    private String password; // Optional - chỉ validate nếu có giá trị (để trống nếu không đổi)

    private Status status; // Optional
}

