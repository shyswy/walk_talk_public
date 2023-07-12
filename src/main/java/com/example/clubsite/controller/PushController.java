package com.example.clubsite.controller;

import com.example.clubsite.dto.fcm.FcmTokenDTO;
import com.example.clubsite.dto.response.RootResponse;
import com.example.clubsite.dto.security.MemberAuthDTO;
import com.example.clubsite.service.clubmember.ClubMemberService;
import com.example.clubsite.utility.ResponseUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Log4j2
@RequiredArgsConstructor
@RequestMapping("api/v1/push")
@Api(tags = "FCM Push 컨트롤러")
public class PushController {
    private final ClubMemberService clubMemberService;

    @ApiOperation(value="fcm 토큰 Push", notes="모바일 기기의 fcm 토큰을 받아옵니다.")
    @PostMapping("devicetoken/update")   // fcm 토큰, device token( 불필요 )
    public ResponseEntity<RootResponse> PushDeviceToken(@AuthenticationPrincipal MemberAuthDTO memberAuthDTO, @RequestBody FcmTokenDTO fcmTokenDTO) {
        clubMemberService.updateFcmToken(memberAuthDTO.getId(), fcmTokenDTO.getFcmToken());
        return ResponseUtil.getInstance().getResponseEntity(HttpStatus.OK, null, null);
    }
}

