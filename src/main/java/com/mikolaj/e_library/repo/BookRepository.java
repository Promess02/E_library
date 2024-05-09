package com.mikolaj.e_library.repo;

import com.mikolaj.e_library.model.Book;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Integer> {

    Page<Book> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    Page<Book> findByBookAuthorContainingIgnoreCase(String filter, PageRequest of);

    Page<Book> findByTitleStartingWithIgnoreCase(String filter, Pageable pageable);
    List<Book> findByTitleStartingWithIgnoreCase(String filter);

}