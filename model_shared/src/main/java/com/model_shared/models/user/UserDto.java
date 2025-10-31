package com.model_shared.models.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Set;
import java.time.LocalDate;
import com.model_shared.enums.Gender;
import java.util.Map;
import com.model_shared.enums.Type;
import com.model_shared.enums.Role;
import com.model_shared.enums.Permission;
import com.model_shared.enums.Status;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
public class UserDto {
    private Type type;
    private Integer userId;
    private String username;
    private String firstName;
    private String lastName;
    private Integer age;
    private Gender gender;
    private LocalDate birth;
    private Role role;
    private Set<Permission> permissions;
    private Status status;
    private Map<String, Object> profileData;
}
