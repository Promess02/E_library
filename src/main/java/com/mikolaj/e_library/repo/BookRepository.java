package com.mikolaj.e_library.repo;

import com.mikolaj.e_library.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Integer> {
}