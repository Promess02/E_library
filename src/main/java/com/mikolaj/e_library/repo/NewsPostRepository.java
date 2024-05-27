package com.mikolaj.e_library.repo;

import com.mikolaj.e_library.model.NewsPost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface NewsPostRepository extends JpaRepository<NewsPost, Integer> {
    List<NewsPost> findNewsPostsByCreateTimeBetween(LocalDate startDate, LocalDate endDate);
}