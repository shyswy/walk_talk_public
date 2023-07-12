package com.example.clubsite.service.clubmember;

import com.example.clubsite.dto.clubmember.ClubMemberDTO;
import com.example.clubsite.dto.clubmember.ClubMemberWithRankDTO;
import com.example.clubsite.dto.clubmember.Token;
import com.example.clubsite.dto.request.LoginRequest;
import com.example.clubsite.dto.request.ReissueRequest;
import com.example.clubsite.dto.request.test.TestLoginRequest;
import com.example.clubsite.dto.request.UpdateMemberProfileRequest;
import com.example.clubsite.dto.response.LoginResponse;
import com.example.clubsite.dto.response.MemberProfileResponse;
import com.example.clubsite.dto.response.UploadMemberImageResponse;
import com.example.clubsite.dto.security.MemberAuthDTO;
import com.example.clubsite.entity.ClubMember;
import com.example.clubsite.entity.Step;
import com.example.clubsite.entity.UserToken;
import com.example.clubsite.exhandler.errorcode.CommonErrorResponseCode;
import com.example.clubsite.exhandler.exception.RestApiException;
import com.example.clubsite.redis.service.MemberTokenRedisService;
import com.example.clubsite.repository.clubmember.ClubMemberRepository;
import com.example.clubsite.service.socialService.GoogleService;
import com.example.clubsite.service.usertoken.UserTokenService;
import com.example.clubsite.utility.FileUtils;
import com.example.clubsite.utility.JWTUtil;
import com.example.clubsite.utility.PathUtil;
import com.example.clubsite.utility.UrlUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class ClubMemberServiceImpl implements ClubMemberService {
    private final PathUtil pathUtil;
    private final UrlUtil urlUtil;
    private final GoogleService googleService;

    private final ClubMemberRepository memberRepository;
    private final UserTokenService userTokenService;
    private final JWTUtil jwtUtil;
    private final MemberTokenRedisService memberTokenRedisService;

    @Value("${default.image.name}")
    private String DEFAULT_IMAGE;

    @Override
    @Transactional//ok
    public LoginResponse loginOrSignUpGoogle(LoginRequest loginRequest) {
        String accessToken3rd = loginRequest.getAccessToken3rd();
        ClubMemberDTO userInfo = googleService.getClubMemberDtoByGoogleToken(accessToken3rd);
        ClubMember clubMember = getClubMemberWithStepAndUserTokenByEmail(userInfo.getEmail());
        if (clubMember == null) {
            clubMember = saveByDto(userInfo);
            clubMember.changeProfileFileName(DEFAULT_IMAGE);
        }
        Token token = jwtUtil.generateToken(clubMember.getId());
        updateRefreshToken(clubMember, token.refreshToken);
        createStepIfNotExist(clubMember);
        memberTokenRedisService.setMemberTokenCacheByClubMember(clubMember.getUserToken(), clubMember.getId());
        log.info("login accessToken, " + clubMember.getName() + ": " + token.getAccessToken());
        return new LoginResponse(clubMember.getId(), clubMember.getName(), clubMember.getEmail(),
                token.accessToken,
                token.refreshToken);
    }

    @Override
    @Transactional//ok
    public LoginResponse testLoginOrSignUpById(TestLoginRequest testLoginRequest) {
        ClubMember clubMember = getClubMemberWithStepAndUserTokenByEmail(testLoginRequest.getEmail());
        if (clubMember == null) {
            clubMember = saveByDto(ClubMemberDTO.builder()//email, name으로 임의의 유저 DB에 생성
                    .email(testLoginRequest.getEmail())
                    .name(testLoginRequest.getName())
                    .build());
            clubMember.changeProfileFileName(DEFAULT_IMAGE);
        }
        Token token = jwtUtil.generateToken(clubMember.getId());
        updateRefreshToken(clubMember, token.refreshToken);
        createStepIfNotExist(clubMember);
        memberTokenRedisService.setMemberTokenCacheByClubMember(clubMember.getUserToken(), clubMember.getId());
        log.info("login accessToken, " + clubMember.getName() + ": " + token.getAccessToken());
        return new LoginResponse(clubMember.getId(), clubMember.getName(), clubMember.getEmail(),
                token.accessToken,
                token.refreshToken);
    }

    @Override
    @Transactional//ok
    public void memberProfileUpdate(MemberAuthDTO memberAuthDTO, UpdateMemberProfileRequest updateMemberProfileRequest) {
        Long id = memberAuthDTO.getId();
        ClubMember findMember = this.getById(id);
        findMember.changeName(updateMemberProfileRequest.getName());
        if (updateMemberProfileRequest.getProfileImageFileUrl() == null) {
            findMember.changeProfileFileName(DEFAULT_IMAGE);
        } else {
            String[] splittedFileName = updateMemberProfileRequest.getProfileImageFileUrl().split("/");
            String simpleFileName = splittedFileName[splittedFileName.length - 1];
            findMember.changeProfileFileName(simpleFileName);
        }
    }

    @NotNull
    @Override
    @Transactional
    public UploadMemberImageResponse uploadProfileImage(MemberAuthDTO memberAuthDTO, MultipartFile profileImageFile) {
        try {
            FileUtils.getInstance().makeDirIfNeeded(pathUtil.getProfileImagePath(null));
            if (!profileImageFile.isEmpty()) {
                String originalFileName = profileImageFile.getOriginalFilename();
                String modifiedFileName = FileUtils.getInstance().getModifiedFileName(originalFileName);
                String fullPath = pathUtil.getProfileImagePath(modifiedFileName);
                profileImageFile.transferTo(new File(fullPath));
                Long id = memberAuthDTO.getId();
                ClubMember findMember = this.getById(id);
                findMember.changeProfileFileName(modifiedFileName);
                this.save(findMember);
                String image = urlUtil.getProfileUrlWithUrl(modifiedFileName);
                return new UploadMemberImageResponse(image);
            } else throw new RestApiException(CommonErrorResponseCode.INVALID_PARAMETER);
        } catch (IOException e) {
            throw new RestApiException(CommonErrorResponseCode.IO_ERROR, e.getMessage());
        }
    }

    @Override
    @Transactional//ok
    public Token memberReissue(ReissueRequest reissueRequest) { //member, userToken 한번에 fetch join!
        Long id = validateAndExtractTokens(reissueRequest);
        ClubMember clubMember = getByIdWithUserToken(id);
        String refreshToken = clubMember.getUserToken().getRefreshToken();
        isExistRefreshTokenInDB(reissueRequest, refreshToken);
        Token token = jwtUtil.generateToken(id);
        this.updateRefreshToken(clubMember, token.refreshToken);
        this.save(clubMember); //Token이 변경감지로 업데이트 x
        return token;
    }

    @Override
    public ClubMember saveByDto(ClubMemberDTO clubMemberDTO) {
        return memberRepository.save(dtoToEntity(clubMemberDTO));
    }

    @Override
    public void save(ClubMember clubMember) {
        memberRepository.save(clubMember);
    }

    @Override
    public Optional<ClubMember> getByEmail(String email) {
        return memberRepository.findFirstByEmail(email.toLowerCase());
    }

    @Override
    @Transactional(readOnly = true)
    public ClubMemberDTO getdtoByEmail(String email) {//of test
        return ClubMemberDTO.of(memberRepository.findFirstByEmail(email.toLowerCase()).orElse(null), urlUtil);
    }

    @Override
    @Transactional(readOnly = true)
    public ClubMemberDTO getDtoById(Long id) {//of test
        return ClubMemberDTO.of(memberRepository.findById(id).orElseThrow(() -> new RestApiException(CommonErrorResponseCode.RESOURCE_NOT_FOUND)), urlUtil);
    }

    @Override
    @Transactional(readOnly = true)//ok
    public ClubMember getById(Long id) {
        if (id == null) {
            throw new RestApiException(CommonErrorResponseCode.INVALID_PARAMETER);
        }
        return memberRepository.findById(id).orElseThrow(() -> new RestApiException(CommonErrorResponseCode.RESOURCE_NOT_FOUND));
    }

    @Override
    public void modify(ClubMemberDTO clubMemberDTO) {
        ClubMember clubMember = getByEmail(clubMemberDTO.getEmail()).orElseThrow(() -> new RestApiException(CommonErrorResponseCode.RESOURCE_NOT_FOUND));
        clubMember.changeName(clubMemberDTO.getName());
        clubMember.changeProfileFileName(clubMemberDTO.getProfileImageFileUrl());
        memberRepository.save(clubMember); //이미지 제거 (null 변경 시)하면 변경감지가 동작하지 않는다.
    }

    @Override
    public void remove(Long id) {
        ClubMember clubMember = memberRepository.findById(id).orElseThrow(() -> new RestApiException(CommonErrorResponseCode.RESOURCE_NOT_FOUND));
        if (clubMember != null) {
            memberRepository.delete(clubMember);
        }
    }

    @Override
    public void removeByEmail(String email) {
        ClubMember findMember = getByEmail(email).orElseThrow(() -> new RestApiException(CommonErrorResponseCode.RESOURCE_NOT_FOUND));
        if (findMember != null) {
            memberRepository.delete(findMember);
        }
    }

    @Transactional(readOnly = true)//ok
    public List<ClubMember> getMembersEntitiesByIds(List<Long> memberIds) {
        List<ClubMember> clubMembers = memberRepository.findAllByIdIn(memberIds);
        if (clubMembers.size() != memberIds.size() || clubMembers.isEmpty())
            throw new RestApiException(CommonErrorResponseCode.RESOURCE_NOT_FOUND);
        return clubMembers;
    }

    @Override
    @Transactional//왜 변경감지가 발생하지 않을까?
    public void updateRefreshToken(ClubMember clubMember, String newRefreshToken) { // 토큰 정보가 없을 시 create, 존재하면 update
        if (clubMember.getUserToken() != null) {
            clubMember.getUserToken().changeRefreshToken(newRefreshToken);
            memberTokenRedisService.updateRefreshTokenCacheByClubMember(clubMember.getUserToken(), clubMember.getId());
        } else {
            UserToken userToken = UserToken.builder()
                    .refreshToken(newRefreshToken)
                    .build();
            clubMember.setUserToken(userToken);
            memberTokenRedisService.setMemberTokenCacheByClubMember(userToken, clubMember.getId());
        }
    }

    @Transactional//ok
    @Override
    public void updateFcmToken(Long id, String fcmToken) { // 현재 fcm 업데이트 되어도 캐시 초기화 x 되는 문제. ( 각 유저당 fcm토큰이 고정되어버림. )
        ClubMember clubMember = getByIdWithUserToken(id);
        try {
            if (clubMember.getUserToken() != null) {
                if (fcmToken.equals(clubMember.getUserToken().getFcmToken())) return;
                clubMember.getUserToken().changeFcmToken(fcmToken);
                userTokenService.initializeFcmTokensExceptMe(clubMember.getUserToken()); //한 유저가 fcm토큰 다수 가능하도록 변경하면 이렇게 지울 필요 x
                //memberTokenRedisService.updateFcmTokenCacheByClubMember(clubMember.getUserToken(), clubMember.getId());
            } else {
                UserToken userToken = userTokenService.save(UserToken.builder()
                        .fcmToken(fcmToken)
                        .build());
                clubMember.setUserToken(userToken);
                memberTokenRedisService.setMemberTokenCacheByClubMember(userToken, clubMember.getId());
            }
        } catch (Exception e) {
            log.info("updateFcmToken erro!!");
            e.printStackTrace();
        }
    }

    @Override
    @Transactional(readOnly = true)//ok
    public MemberProfileResponse getMemberProfileResponse(Long id) {
        ClubMember findMember = getById(id);
        String image = null;
        if (findMember.getProfileFileName() != null) {
            image = urlUtil.getProfileUrlWithUrl(findMember.getProfileFileName());
        }
        return new MemberProfileResponse(findMember.getId(), findMember.getName(), findMember.getEmail().toLowerCase(), image);
    }

    @Override
    @Transactional
    public void updateStep(Long id, Long stepCount) {
        ClubMember clubMember = getClubMemberWithStepById(id);
        Step step = clubMember.getStep();
        if (step == null) {
            step = Step.builder()
                    // .clubMember(clubMember)
                    .stepCount(stepCount).build();
            clubMember.setStep(step);
        } else {
            step.changeStepCount(stepCount);
        }
    }

    @Override
    public List<ClubMemberDTO> clubMembersToClubMemberDtoS(List<ClubMember> result) {//of test
        return result.stream()
                .map(objects -> ClubMemberDTO.of(objects, urlUtil))
                .collect(Collectors.toList());
    }

    @Override
    public List<ClubMemberWithRankDTO> clubMembersToRankDtoS(List<ClubMember> result) {
        return result.stream()
                .map(objects -> ClubMemberWithRankDTO.of(objects, urlUtil))
                .collect(Collectors.toList());
    }

    @Override
    public ClubMember getClubMemberWithStepById(Long id) {
        ClubMember clubMember = memberRepository.findByIdWithStep(id);
        if (clubMember == null) throw new RestApiException(CommonErrorResponseCode.RESOURCE_NOT_FOUND);
        return clubMember;
    }

    @Override
    @Transactional(readOnly = true)
    public ClubMember getByIdWithUserToken(Long id) {
        ClubMember clubMember = memberRepository.findByIdWithUserToken(id);
        if (clubMember == null) throw new RestApiException(CommonErrorResponseCode.RESOURCE_NOT_FOUND);
        return clubMember;
    }

    @Override
    public ClubMember getClubMemberWithStepAndUserTokenByEmail(String email) {
        return memberRepository.findByEmailWithStepAndUserToken(email);
    }

    @Override
    public void createStepIfNotExist(ClubMember clubMember) {
        Step step = clubMember.getStep();
        if (step == null) {
            step = Step.builder()
                    .stepCount(0L).build();
            clubMember.setStep(step);
        }
    }

    private Long validateAndExtractTokens(ReissueRequest reissueRequest) {
        String refreshToken = reissueRequest.getRefreshToken();
        String accessToken = reissueRequest.getAccessToken();
        if (isTokenNull(refreshToken, accessToken))
            throw new RestApiException(CommonErrorResponseCode.UNAUTHORIZED);
        jwtUtil.validateRefreshToken(refreshToken);
        MemberAuthDTO memberAuthDTO = jwtUtil.extractMemberAuthDTO(accessToken).orElseThrow(() -> new RestApiException(CommonErrorResponseCode.BAD_REQUEST));
        return memberAuthDTO.getId();
    }

    private static boolean isTokenNull(String refreshToken, String accessToken) {
        return (refreshToken == null || accessToken == null);
    }

    private void isExistRefreshTokenInDB(ReissueRequest reissueRequest, String refreshToken) {
        if (!refreshToken.equals(reissueRequest.getRefreshToken())) {
            throw new RestApiException(CommonErrorResponseCode.UNAUTHORIZED);
        }
    }
}



