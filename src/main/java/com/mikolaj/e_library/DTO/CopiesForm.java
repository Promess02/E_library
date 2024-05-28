package com.mikolaj.e_library.DTO;

import lombok.Data;

@Data
public class CopiesForm {
    private int copyId;
    private String shelfPlace;
    private int bookId;
    private String rentalStatus;
    private String qualityStatus;
    private int workerId;
}
