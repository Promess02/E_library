package com.mikolaj.e_library.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookRatingForm {
    private int rating;
    private int bookId;
    private int readerId;
}
