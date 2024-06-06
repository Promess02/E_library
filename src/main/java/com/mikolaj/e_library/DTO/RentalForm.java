package com.mikolaj.e_library.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RentalForm {
    String readerEmail;
    int bookId;
    int rentalInWeeks;
}
