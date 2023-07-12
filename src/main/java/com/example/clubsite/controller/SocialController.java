package com.example.clubsite.controller;

import com.example.clubsite.dto.request.LoginRequest;
import com.example.clubsite.dto.request.test.TestMemberDeleteRequest;
import com.example.clubsite.dto.response.RootResponse;
import com.example.clubsite.service.clubmember.ClubMemberService;
import com.example.clubsite.utility.ResponseUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("api/v1")
@RequiredArgsConstructor
@Api(tags = "로그인 로직 컨트롤러")
public class SocialController {
    private final ClubMemberService clubMemberService;
    @Value("${spring.profile}")
    private String profile;
    @Value("${spring.common.profile}")
    private String common;

    @PostMapping("config")
    public ResponseEntity<RootResponse> config() {
        return ResponseUtil.getInstance().getResponseEntity(HttpStatus.UNAUTHORIZED, null, null);
    }

//    @ApiResponses({
//            @ApiResponse(code=200, message="성공"),
//            @ApiResponse(code=401, message="접근 권한이 없습니다.")
//    })
    @ApiOperation(value="소셜 로그인 [테스트 시 아래의 테스트 컨트롤러 -> 테스트 로그인 사용]", notes="google accessToken를 받아와서 소셜 로그인 기능을 수행합니다.")
    @PostMapping("login")
    public ResponseEntity<RootResponse> login(@RequestBody LoginRequest loginRequest) {
        return ResponseUtil.getInstance().getResponseEntity(HttpStatus.OK, clubMemberService.loginOrSignUpGoogle(loginRequest), null);
    }
}





