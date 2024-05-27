package com.mikolaj.e_library.repo;

import com.mikolaj.e_library.model.Book;
import com.mikolaj.e_library.model.BookRating;
import com.mikolaj.e_library.model.Reader;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookRatingRepository extends JpaRepository<BookRating, Integer> {
    List<BookRating> getBookRatingsByBook(Book book);

    Optional<BookRating> findByBookAndReader(Book book, Reader reader);
}