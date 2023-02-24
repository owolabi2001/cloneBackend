package com.clone.cloneBackend.dto;

import com.clone.cloneBackend.domain.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegistrationDto {

    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String dateOfBirth;
    private String gender;
    private Role role;

}
