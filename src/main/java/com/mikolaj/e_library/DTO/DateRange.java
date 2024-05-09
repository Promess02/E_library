package com.mikolaj.e_library.DTO;

import lombok.Data;

import java.time.LocalDate;

@Data
public class DateRange {
    LocalDate startDate;
    LocalDate endDate;
}
