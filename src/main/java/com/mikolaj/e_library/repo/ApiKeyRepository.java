package com.mikolaj.e_library.repo;

import com.mikolaj.e_library.model.ApiKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ApiKeyRepository extends JpaRepository<ApiKey, Integer> {
    Optional<ApiKey> findByApiKey(String apiKey);
    Optional<ApiKey> findByApiKeyAndStatusAndUserType(String apiKey, String status, String userType);
    Optional<ApiKey> findByApiKeyAndStatus(String apiKey, String status);
    void deleteAllByUserIdAndStatus(Integer userId, String status);
    List<ApiKey> findByUserId(int userId);
}