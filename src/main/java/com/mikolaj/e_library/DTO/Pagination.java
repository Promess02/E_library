package com.mikolaj.e_library.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Pagination {
    private int size;
    private int page;
    private String filter;
    private String filterBy;
}
