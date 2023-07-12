package com.example.clubsite.service.usertoken;

import com.example.clubsite.entity.UserToken;

import java.util.List;

public interface UserTokenService {
    UserToken save(UserToken userToken);

    List<UserToken> getByFcmToken(String fcmToken);

    void initializeFcmTokensExceptMe(UserToken userToken);
}
