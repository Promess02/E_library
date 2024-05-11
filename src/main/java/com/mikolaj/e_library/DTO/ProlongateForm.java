package com.mikolaj.e_library.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProlongateForm {
    int rentalId;
    int prolongationInWeeks;
}
