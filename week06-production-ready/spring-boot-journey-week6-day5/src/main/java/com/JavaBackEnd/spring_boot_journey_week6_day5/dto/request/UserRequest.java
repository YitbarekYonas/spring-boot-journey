package com.JavaBackEnd.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "password")
public class UserRequest {
    private String name;
    
    @Email(message = "Invalid email")
    private String email;
    
    @NotBlank(message = "Password is required")
    private String password;
}