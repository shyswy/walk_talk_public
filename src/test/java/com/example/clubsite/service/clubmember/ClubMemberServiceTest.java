package com.example.clubsite.service.clubmember;

import com.example.clubsite.dto.clubmember.ClubMemberDTO;
import com.example.clubsite.dto.clubmember.Token;
import com.example.clubsite.dto.request.LoginRequest;
import com.example.clubsite.dto.response.LoginResponse;
import com.example.clubsite.entity.ClubMember;
import com.example.clubsite.entity.UserToken;
import com.example.clubsite.enumType.SocialType;
import com.example.clubsite.redis.service.MemberTokenRedisService;
import com.example.clubsite.repository.clubmember.ClubMemberRepository;
import com.example.clubsite.service.socialService.GoogleService;
import com.example.clubsite.service.usertoken.UserTokenService;
import com.example.clubsite.utility.JWTUtil;
import com.example.clubsite.utility.PathUtil;
import com.example.clubsite.utility.UrlUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@Slf4j
public class ClubMemberServiceTest {

    public static final String ACCESS_TOKEN_PREFIX = "testAccessToken:";
    public static final String REFRESH_TOKEN_PREFIX = "testRefreshToken:";
    @Mock
    private PathUtil pathUtil;

    @Mock
    private UrlUtil urlUtil;

    @Mock
    private GoogleService googleService;

    @Mock
    private ClubMemberRepository memberRepository;

    @Mock
    private UserTokenService userTokenService;

    @Mock
    private JWTUtil jwtUtil;

    @Mock
    private MemberTokenRedisService memberTokenRedisService;


    @InjectMocks//@Mock이나 @Spy로 선언된 모의 객체(Mock or Spy)들을 자동 주입 -> @Autowired를 통한 실제 객체는 주입 x
    private ClubMemberServiceImpl clubMemberService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testLoginAndGetLoginResponse() {
        for (int i = 0; i < 2; i++) {
            if (i == 0) log.info("not found-register test===========");
            if (i == 1) log.info("found-findMember test===========");

            // given
            String email = "test1@gmail.com";
            String name = "test1", findName = "findNameForTest1"; //fidMember 가 null 인 케이스 와 아닌 케이스 for 문으로?
            String imageName = "imageNameExample", findImageName = "findImageForTest1";
            String googleToken = "defaultGoogleToken";
            Long memberId = 501L;
            Long step = 11L;
            String expectedName = "";
            if (i == 0) expectedName = name;
            else expectedName = findName;

            LoginRequest loginRequest = LoginRequest.builder()
                    .socialType(SocialType.GOOGLE)
                    .accessToken3rd(googleToken)
                    .build();

            ClubMemberDTO userInfoFromGoogleApi = makeClubMemberDTO(email, name, imageName, step);

            ClubMember findClubMember = null;
            if (i == 1) {
                findClubMember = makeClubMember(email, findName, findImageName);
            }

            Token token = Token.builder()
                    .refreshToken(REFRESH_TOKEN_PREFIX + memberId)
                    .accessToken(ACCESS_TOKEN_PREFIX + memberId)
                    .build();


            // when
            when(urlUtil.getProfileUrlWithUrl(anyString())).thenAnswer(invocation -> invocation.getArgument(0));
            when(jwtUtil.generateToken(any())).thenAnswer(invocation -> {
//                Long givenMemberId = invocation.getArgument(0);//파라미터로 들어온 Long을 기반으로 토큰 생성을 위해 작성함.
                // if(givenMemberId==null) givenMemberId=memberId; //id는 auto generate라 null 이 들어온다.( save 가 mock이기에 )
                return token;
            });
            when(googleService.getClubMemberDtoByGoogleToken(googleToken)).thenReturn(userInfoFromGoogleApi);
            when(clubMemberService.getClubMemberWithStepAndUserTokenByEmail(anyString())).thenReturn(findClubMember); // 해당 유저가 존재하지 않을 때 (Null) 시 register 로직 실행
            when(memberRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));//save가 실제 db 젒근 x 따라서 auto 생성 memberId 실패해서 memberId null 된다.
            doNothing().when(memberTokenRedisService).setMemberTokenCacheByClubMember(any(UserToken.class), anyLong()); // void 메소드가 아무것도 수행하지 않게 하기
            doNothing().when(memberTokenRedisService).updateRefreshTokenCacheByClubMember(any(UserToken.class), anyLong());


            // then
            LoginResponse actualResponse = clubMemberService.loginOrSignUpGoogle(loginRequest);
            LoginResponse expectedResponse = LoginResponse.builder()
                    .email(email)
                    .name(expectedName)
                    .accessToken(ACCESS_TOKEN_PREFIX + memberId)
                    .refreshToken(REFRESH_TOKEN_PREFIX + memberId)
                    .id(memberId)
                    .build();

            log.info("test result: {}", actualResponse.toString());
            // assertEquals(expectedResponse.getId(), actualResponse.getId()); // register시, id는 auto generate 됨 -> db mock라 null 된다.
            assertEquals(expectedResponse.getName(), actualResponse.getName());
            assertEquals(expectedResponse.getEmail(), actualResponse.getEmail());
            assertEquals(expectedResponse.getAccessToken(), actualResponse.getAccessToken());
            assertEquals(expectedResponse.getRefreshToken(), actualResponse.getRefreshToken());
        }
        verify(googleService, times(2)).getClubMemberDtoByGoogleToken(anyString()); //등록 케이스, 존재하서 find case 에서 2번
        verify(jwtUtil, times(2)).generateToken(any()); //각각 토큰 생성.
        verify(memberRepository, times(1)).save(any()); //save() 는 등록에서만 발생한다.
    }

    private static ClubMember makeClubMember(String email, String name, String imageName) {
        ClubMember registeredClubMember = ClubMember.builder()
                .email(email)
                .name(name)
                .profileFileName(imageName)
                .build();
        return registeredClubMember;
    }

    private ClubMemberDTO makeClubMemberDTO(String email, String name, String imageName, Long step) {
        ClubMemberDTO userInfoFromGoogleApi = ClubMemberDTO.builder()
                .email(email)
                .name(name)
                .profileImageFileUrl(urlUtil.getProfileUrlWithUrl(imageName))
                .stepCount(step)
                .build();
        return userInfoFromGoogleApi;
    }
}



