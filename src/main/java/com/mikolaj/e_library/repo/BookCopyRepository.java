package com.mikolaj.e_library.repo;

import com.mikolaj.e_library.model.Book;
import com.mikolaj.e_library.model.BookCopy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookCopyRepository extends JpaRepository<BookCopy, Integer> {
    public List<BookCopy> findBookCopiesByBook(Book book);
}