package com.example.clubsite.controller.test;

import com.example.clubsite.dto.request.test.TestLoginRequest;
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
@RequestMapping("test")
@RequiredArgsConstructor
@Api(tags = "테스트 로그인 컨트롤러")
public class testLoginController {//hello window
    private final ClubMemberService clubMemberService;
    @Value("${spring.profile}")
    private String profile;
    @Value("${spring.common.profile}")
    private String common;

    @ApiOperation(value="테스트 로그인 [Authorize 불필요] ", notes="이메일 입력 시, 이미 존재하는 유저라면 로그인을 수행하고, 존재하지 않는 유저는 DB에 추가합니다. \n그리고 access 토큰과 refresh 토큰을 발급해줍니다.")
    @PostMapping("login")//test login
    public ResponseEntity<RootResponse> login(@RequestBody TestLoginRequest testLoginRequest) {
        return ResponseUtil.getInstance().getResponseEntity(HttpStatus.OK, clubMemberService.testLoginOrSignUpById(testLoginRequest), null);
    }

    @ApiOperation(value="테스트 회원 탈퇴 [Authorize 불필요] ", notes="이메일을 입력하여 회원 탈퇴를 수행합니다.")
    @PostMapping("delete")
    public ResponseEntity<RootResponse> delete(@RequestBody TestMemberDeleteRequest testMemberDeleteRequest) {
        clubMemberService.removeByEmail(testMemberDeleteRequest.getEmail());
        return ResponseUtil.getInstance().getResponseEntity(HttpStatus.OK, null, null);
    }
}
