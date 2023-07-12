package com.example.clubsite.service.usertoken;

import com.example.clubsite.entity.UserToken;
import com.example.clubsite.redis.service.MemberTokenRedisService;
import com.example.clubsite.repository.UserTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Log4j2
@RequiredArgsConstructor
public class UserTokenServiceImpl implements UserTokenService {
    private final UserTokenRepository userTokenRepository;
    private final MemberTokenRedisService memberTokenRedisService;

    @Override
    public UserToken save(UserToken userToken) {
        return userTokenRepository.save(userToken);
    }

    public List<UserToken> getByFcmToken(String fcmToken) {
        return userTokenRepository.findByFcmToken(fcmToken);
    }

    @Override
    public void initializeFcmTokensExceptMe(UserToken userToken) {
        memberTokenRedisService.deleteMemberTokenCachesExceptMeByFcmToken(userToken.getFcmToken(), userToken.getId());
        List<UserToken> sameUserTokens = getByFcmToken(userToken.getFcmToken());
        initializeFcmTokensById(userToken.getId(), sameUserTokens);
    }

    private void initializeFcmTokensById(Long userTokenId, List<UserToken> sameUserTokens) {
        if (!sameUserTokens.isEmpty()) {
            for (UserToken sameUserToken : sameUserTokens) {
                Long findUserTokenId = sameUserToken.getId();
                if (findUserTokenId.equals(userTokenId)) continue;
                sameUserToken.changeFcmToken(null);
                //redisService.deleteMemberCacheByUserTokenId(findUserTokenId); //굳이 지워야하나? 자기한테도오는건 캐시 때문으로 추정
            }
        }
    }
}
