package com.mikolaj.e_library.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WorkerRegistrationForm {
    private String workerType;
    private String name;
    private String phoneNumber;
    private String email;
    private String password;
    private String surname;
    private String address;
    private String pesel;
    private String payAccountNumber;
    private int userId;
    private int employerId;
    private int monthlyPay;
}
