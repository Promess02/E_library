package com.mikolaj.e_library.repo;

import com.mikolaj.e_library.model.BookRating;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRatingRepository extends JpaRepository<BookRating, Integer> {
}