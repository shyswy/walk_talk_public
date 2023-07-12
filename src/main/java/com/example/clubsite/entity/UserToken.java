package com.example.clubsite.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter

public class UserToken extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "token_id")
    private Long id;
    private String refreshToken;
    private String fcmToken;

    public void changeRefreshToken(String newRefreshToken) {
        this.refreshToken = newRefreshToken;
    }

    public void changeFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }
}
