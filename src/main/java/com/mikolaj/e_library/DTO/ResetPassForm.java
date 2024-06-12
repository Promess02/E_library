package com.mikolaj.e_library.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResetPassForm {
    private String oldPass;
    private String newPass;
    private String email;

    public ResetPassForm(String oldPass, String newPass) {
        this.oldPass = oldPass;
        this.newPass = newPass;
    }
}
