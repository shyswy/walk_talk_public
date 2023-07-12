package com.example.clubsite.repository;

import com.example.clubsite.entity.UserToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserTokenRepository extends JpaRepository<UserToken, Long> {
    List<UserToken> findByFcmToken(String fcmToken);

    void deleteAllById(Long id);

    void deleteAllByFcmToken(String fcmToken);
}
