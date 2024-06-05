package com.mikolaj.e_library.repo;

import com.mikolaj.e_library.DTO.RentalStatus;
import com.mikolaj.e_library.model.Book;
import com.mikolaj.e_library.model.BookCopy;
import com.mikolaj.e_library.model.Reader;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookCopyRepository extends JpaRepository<BookCopy, Integer> {
    List<BookCopy> findBookCopiesByBook(Book book);
    List<BookCopy> findBookCopiesByReaderAndRentalStatus(Reader reader, RentalStatus rentalStatus);
}