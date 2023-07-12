package com.example.clubsite.controller;

import com.example.clubsite.dto.request.EmailRequest;
import com.example.clubsite.dto.response.RootResponse;
import com.example.clubsite.dto.security.MemberAuthDTO;
import com.example.clubsite.service.friend.FriendService;
import com.example.clubsite.utility.ResponseUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Log4j2
@RequestMapping("api/v1/user/friend")
@Api(tags = "친구 로직 컨트롤러")
public class FriendController {
    private final FriendService friendService;

    @ApiOperation(value="친구 목록 조회", notes="자신과 맞팔로우를 통해 친구인 모든 유저를 조회합니다.")
    @GetMapping("friend-lists")
    public ResponseEntity<RootResponse> getFriendList(@AuthenticationPrincipal MemberAuthDTO memberAuthDTO) {
        return ResponseUtil.getInstance().getResponseEntity(HttpStatus.OK, friendService.getFriendList(memberAuthDTO.getId()), null);
    }

    @ApiOperation(value="친구 요청 목록 조회", notes="자신이 팔로우 중인 유저의 목록을 조회합니다.")
    @GetMapping("request-lists")
    public ResponseEntity<RootResponse> getRequestList(@AuthenticationPrincipal MemberAuthDTO memberAuthDTO) {
        return ResponseUtil.getInstance().getResponseEntity(HttpStatus.OK, friendService.getFriendRequestList(memberAuthDTO.getId()), null);
    }

    @ApiOperation(value="전체 친구 목록 조회", notes="자신이 팔로우 중인 유저 리스트, 팔로잉 중인 유저 리스트, 맞팔로우 중인 유저리스트를 조회합니다.")
    @GetMapping("all-lists")
    public ResponseEntity<RootResponse> getAllList(@AuthenticationPrincipal MemberAuthDTO memberAuthDTO) {
        return ResponseUtil.getInstance().getResponseEntity(HttpStatus.OK, friendService.findAllRelationShipByMemberId(memberAuthDTO.getId()), null);
    }

    @ApiOperation(value="친구 요청 보내기", notes="자신이 친구를 요청한 유저의 이메일을 통해 친구 요청을 수행합니다.")
    @PostMapping("request") //email (UK로)
    public ResponseEntity<RootResponse> requestFriend(@AuthenticationPrincipal MemberAuthDTO memberAuthDTO,
                                                      @RequestBody EmailRequest emailRequest) {
        return ResponseUtil.getInstance().getResponseEntity(HttpStatus.OK, friendService.requestFriend(memberAuthDTO.getId(), emailRequest.getEmail()), null);
    }

    @ApiOperation(value="친구 수락", notes="자신에게 온 친구 요청을 수락합니다.")
    @PostMapping("accept/{requestMemberId}")
    public ResponseEntity<RootResponse> acceptFriend(@AuthenticationPrincipal MemberAuthDTO memberAuthDTO,
                                                     @PathVariable("requestMemberId") Long requestMemberId) {
        return ResponseUtil.getInstance().getResponseEntity(HttpStatus.OK, friendService.acceptFriend(memberAuthDTO.getId(), requestMemberId), null);
    }

    @ApiOperation(value="친구 끊기", notes="자신의 친구와 친구 관계를 종료합니다.")
    @DeleteMapping("{memberId}")
    public ResponseEntity<RootResponse> deleteFriend(@AuthenticationPrincipal MemberAuthDTO memberAuthDTO,
                                                     @PathVariable("memberId") Long memberId) {
        friendService.removeFriendShip(memberAuthDTO.getId(), memberId);
        return ResponseUtil.getInstance().getResponseEntity(HttpStatus.OK, null, null);
    }
}


