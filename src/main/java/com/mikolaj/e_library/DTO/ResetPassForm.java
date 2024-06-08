package com.mikolaj.e_library.DTO;

import lombok.Data;

@Data
public class ResetPassForm {
    private String oldPass;
    private String newPass;
    private String email;
}
