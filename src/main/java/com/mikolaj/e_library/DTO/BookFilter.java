package com.mikolaj.e_library.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookFilter {
    private int size;
    private int page;
    private String filter;
    private String filterBy;
    private String author;
    private String bookCategory;
    private String bookType;
    private Float minBookRating;
}
