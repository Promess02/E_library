package com.mikolaj.e_library.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RentalForm {
    int readerId;
    int bookId;
    int rentalInWeeks;
}
