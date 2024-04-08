package com.mikolaj.e_library.repo;

import com.mikolaj.e_library.model.BookCopy;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookCopyRepository extends JpaRepository<BookCopy, Integer> {
}