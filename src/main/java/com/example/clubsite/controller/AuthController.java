package com.example.clubsite.controller;

import com.example.clubsite.dto.request.ReissueRequest;
import com.example.clubsite.dto.response.RootResponse;
import com.example.clubsite.service.clubmember.ClubMemberService;
import com.example.clubsite.utility.ResponseUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("auth")
@Api(tags = "인증 로직 컨트롤러")
public class AuthController {
    private final ClubMemberService clubMemberService;

    @ApiOperation(value="access 토큰 재발급 [Authorize 불필요]", notes="access 토큰 만료시, refresh 토큰으로 재발급 받습니다.")
    @PostMapping("reissue")
    public ResponseEntity<RootResponse> reissue(@RequestBody ReissueRequest reissueRequest) {
        return ResponseUtil.getInstance().getResponseEntity(HttpStatus.OK, clubMemberService.memberReissue(reissueRequest), null);
    }
}


