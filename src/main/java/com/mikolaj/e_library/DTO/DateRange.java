package com.mikolaj.e_library.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class DateRange {
    LocalDate startDate;
    LocalDate endDate;
}
