package com.example.clubsite.redis.clubmemberdto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor

@RedisHash(value = "member-token")
public class MemberTokenCacheDTO {//@TimeToLive -> TTl

    @Id
    private Long id;

    private Long tokenId;

    @Indexed
    private String fcmToken;

    private String refreshToken;

    public void changeFcmToken(String fcmToken){
        this.fcmToken=fcmToken;
    }

    public void changeRefreshToken(String refreshToken){
        this.refreshToken=refreshToken;
    }
}
