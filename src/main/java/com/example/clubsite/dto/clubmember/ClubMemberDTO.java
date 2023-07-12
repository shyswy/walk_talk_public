package com.example.clubsite.dto.clubmember;

import com.example.clubsite.entity.ClubMember;
import com.example.clubsite.entity.Step;
import com.example.clubsite.entity.UserToken;
import com.example.clubsite.exhandler.errorcode.CommonErrorResponseCode;
import com.example.clubsite.exhandler.exception.RestApiException;
import com.example.clubsite.utility.UrlUtil;
import lombok.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.util.Optional;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Log4j2
@ToString
@RedisHash(value = "member-cache")
public class ClubMemberDTO {

    @Id
    private Long id;
    private String email;
    private String name;
    private Long stepCount;
    private String refreshToken;
    private String profileImageFileUrl;
    private Long tokenId;
    @Indexed
    private String fcmToken;

    public void changeName(String name) {
        this.name = name;
    }

    public void changeFcmToken(String fcmToken){
        this.fcmToken=fcmToken;
    }

    public void changeRefreshToken(String refreshToken){
        this.fcmToken=refreshToken;
    }

    public void changeProfileImageFileUrl(String profileImageFileUrl) {
        this.profileImageFileUrl = profileImageFileUrl;
    }

    public ClubMemberDTO(Long id, String email, String name, String refreshToken, Long stepCount, String fcmToken) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.refreshToken = refreshToken;
        this.stepCount = stepCount;
        this.fcmToken = fcmToken;
    }

//    public static ClubMemberDTO of(ClubMember clubMember, UrlUtil urlUtil) {
//        try {
//            if (clubMember == null) return null;
//            return ClubMemberDTO.builder()
//                    .email(clubMember.getEmail())
//                    .name(clubMember.getName())
//                    .profileImageFileUrl(urlUtil.getProfileUrlWithUrl(clubMember.getProfileFileName()))
//                    .id(clubMember.getId())
//                    .stepCount(Optional.ofNullable(clubMember.getStep())
//                            .map(Step::getStepCount)
//                            .orElse(null))
//                    .build();
//        } catch (Exception e) {
//            log.error("message={}", "message", e);
//            throw new RestApiException(CommonErrorResponseCode.INVALID_PARAMETER, e.getMessage());
//        }
//    }

    public static ClubMemberDTO of(ClubMember clubMember, UrlUtil urlUtil) {
        try {
            if (clubMember == null) return null;
            ClubMemberDTO memberDTO = ClubMemberDTO.builder()
                    .email(clubMember.getEmail())
                    .name(clubMember.getName())
                    .profileImageFileUrl(urlUtil.getProfileUrlWithUrl(clubMember.getProfileFileName()))
                    .id(clubMember.getId())
                    .tokenId(Optional.ofNullable(clubMember.getUserToken())
                            .map(UserToken::getId)
                            .orElse(null))
                    .stepCount(Optional.ofNullable(clubMember.getStep())
                            .map(Step::getStepCount)
                            .orElse(null))
                    .fcmToken(Optional.ofNullable(clubMember.getUserToken())
                            .map(UserToken::getFcmToken)
                            .orElse(null))
                    .refreshToken(Optional.ofNullable(clubMember.getUserToken())
                            .map(UserToken::getRefreshToken)
                            .orElse(null))
                    .build();
            log.info("memberDTO: {}",memberDTO.toString());
            return memberDTO;
        } catch (Exception e) {
            log.error("message={}", "message", e);
            throw new RestApiException(CommonErrorResponseCode.INVALID_PARAMETER, e.getMessage());
        }
    }
}
