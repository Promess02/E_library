package com.mikolaj.e_library.DTO;

import lombok.Data;

@Data
public class LoginForm {
    private String email;
    private String password;
    private String userType;
}
