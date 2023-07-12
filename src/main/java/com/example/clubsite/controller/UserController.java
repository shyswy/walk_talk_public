package com.example.clubsite.controller;

import com.example.clubsite.dto.request.StepRequest;
import com.example.clubsite.dto.request.UpdateMemberProfileRequest;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@Log4j2
@RequestMapping("api/v1")
@RequiredArgsConstructor
@Api(tags = "유저 정보 로직 컨트롤러")
public class UserController {
    private final ClubMemberService clubMemberService;

    @ApiOperation(value="자신의 프로필 조회", notes="자신의 프로필 정보를 조회합니다.")
    @GetMapping("user/me")
    public ResponseEntity<RootResponse> getMemberProfile(@AuthenticationPrincipal MemberAuthDTO memberAuthDTO) {
        return ResponseUtil.getInstance().getResponseEntity(HttpStatus.OK, clubMemberService.getMemberProfileResponse(memberAuthDTO.getId()), null);
    }

    @ApiOperation(value="유저 정보 업데이트", notes="유저 프로필 이미지 및 유저 정보 업데이트를 수행합니다.")
    @PostMapping("user/update")   //유저 사진 제거시 업데이트만 수행된다.
    public ResponseEntity<RootResponse> updateMemberProfile(@AuthenticationPrincipal MemberAuthDTO memberAuthDTO, @RequestBody UpdateMemberProfileRequest updateMemberProfileRequest) {
        clubMemberService.memberProfileUpdate(memberAuthDTO, updateMemberProfileRequest);
        return ResponseUtil.getInstance().getResponseEntity(HttpStatus.OK, null, null);
    }

    @ApiOperation(value="유저 이미지 업로드", notes="해당 유저의 이미지를 업로드 합니다.")
    @PostMapping("user/upload/image")
    public ResponseEntity<RootResponse> uploadMemberImage(@AuthenticationPrincipal MemberAuthDTO memberAuthDTO, @RequestParam MultipartFile profileImageFile) {
        return ResponseUtil.getInstance().getResponseEntity(HttpStatus.OK, clubMemberService.uploadProfileImage(memberAuthDTO, profileImageFile), null);
    }

    @ApiOperation(value="유저 걸음 수 업데이트", notes="해당 유저의 걸음 수 정보를 받아와 업데이트 합니다.")
    @PostMapping("user/stepcount/update")
    public ResponseEntity<RootResponse> updateStepCount(@AuthenticationPrincipal MemberAuthDTO memberAuthDTO, @RequestBody StepRequest stepRequest) {
        clubMemberService.updateStep(memberAuthDTO.getId(), stepRequest.getStepCount());
        return ResponseUtil.getInstance().getResponseEntity(HttpStatus.OK, null, null);
    }
}

