package com.example.springlibrary.security.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequestDto {
    @NotBlank
    private String firstname;
    @NotBlank
    private String lastname;


    @Email
    @NotBlank
    private String email;
    @NotBlank
    private String password;
}
