package com.mikolaj.e_library.DTO;

import lombok.Data;

@Data
public class WorkerRentalForm {
    private int bookId;
    private String readerEmail;
    private int workerId;
    private int rentalInWeeks;

    public WorkerRentalForm() {
    }

    public WorkerRentalForm(int bookId, String readerEmail, int workerId, int rentalInWeeks) {
        this.bookId = bookId;
        this.readerEmail = readerEmail;
        this.workerId = workerId;
        this.rentalInWeeks = rentalInWeeks;
    }
}
