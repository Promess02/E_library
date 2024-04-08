package com.mikolaj.e_library.repo;

import com.mikolaj.e_library.model.NewsPost;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NewsPostRepository extends JpaRepository<NewsPost, Integer> {
}