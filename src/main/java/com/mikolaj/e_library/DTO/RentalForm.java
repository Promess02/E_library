package com.mikolaj.e_library.DTO;

import lombok.Data;

@Data
public class RentalForm {
    int readerId;
    int bookId;
    int rentalInWeeks;
}
