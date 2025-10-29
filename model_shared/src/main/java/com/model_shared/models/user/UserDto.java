package com.model_shared.models.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;
import java.time.LocalDate;
import com.model_shared.enums.Gender;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
public class UserDto {
    private Integer userId;
    private String userName;
    private String firstName;
    private String lastName;
    private Integer age;
    private Gender gender;
    private LocalDate birth;
    private String role;
    private List<String> permissions;
    private boolean enabled;
    private Map<String, Object> profileData;
}
