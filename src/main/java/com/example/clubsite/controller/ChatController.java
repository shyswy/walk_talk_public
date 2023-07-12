package com.example.clubsite.controller;

import com.example.clubsite.dto.request.MemberIdsChatRoomRequest;
import com.example.clubsite.dto.request.MemberIdsRequest;
import com.example.clubsite.dto.request.MessageChatIdRequest;
import com.example.clubsite.dto.request.RankNumChatRoomIdRequest;
import com.example.clubsite.dto.response.RootResponse;
import com.example.clubsite.dto.security.MemberAuthDTO;
import com.example.clubsite.service.chat.ChatService;
import com.example.clubsite.service.memberchatroom.MemberChatRoomService;
import com.example.clubsite.utility.ResponseUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;

@RestController
@Log4j2
@RequiredArgsConstructor
@RequestMapping("api/v1/user")
@Api(tags = "채팅 로직 컨트롤러")
public class ChatController {
    private final ChatService chatService;
    private final MemberChatRoomService memberChatRoomService;

    @ApiOperation(value="채팅방 생성", notes="채팅방 내 멤버들의 아이디와 채팅방 이름으로 채팅방을 생성합니다.")
    @PostMapping(value = {"chat/create"})
    public ResponseEntity<RootResponse> createChatRoom(@AuthenticationPrincipal MemberAuthDTO memberAuthDTO, @RequestBody MemberIdsRequest request) {
        return ResponseUtil.getInstance().getResponseEntity(HttpStatus.OK, chatService.makeChatRoom(memberAuthDTO.getId(), request), null);
    }

    @ApiOperation(value="채팅방 진입", notes="채팅방의 id를 받아와 해당 채팅방에 진입합니다.")
    @GetMapping(value = {"chat/enter/{chatRoomId}"})
    public ResponseEntity<RootResponse> enterChatRoom(@AuthenticationPrincipal MemberAuthDTO memberAuthDTO, @PathVariable Long chatRoomId) {
        return ResponseUtil.getInstance().getResponseEntity(HttpStatus.OK, chatService.enterChatRoom(chatRoomId, memberAuthDTO.getId()), null);
    }

    @ApiOperation(value="채팅 추가", notes="채팅방의 id와 채팅 내용을 받아와 해당 채팅방에서 자신을 제외한 모든 유저에게 채팅을 보냅니다.")
    @PostMapping("chat/add")
    public ResponseEntity<RootResponse> chatAdd(@AuthenticationPrincipal MemberAuthDTO memberAuthDTO, @RequestBody MessageChatIdRequest request) throws UnsupportedEncodingException {  //body 에 채팅
        chatService.addChat(memberAuthDTO.getId(), request.getChatRoomId(), request.getMessage());
        return ResponseUtil.getInstance().getResponseEntity(HttpStatus.OK, null, null);
    }

    @ApiOperation(value="채팅방 초대", notes="채팅방의 id와 초대할 멤버들의 id를 받아와 해당 채팅방에 새로운 유저를 초대합니다.")
    @PatchMapping("chat/invite")   //임시로 security 풀어줌..!
    public ResponseEntity<RootResponse> inviteMemberToChatRoom(@RequestBody MemberIdsChatRoomRequest request) {
        return ResponseUtil.getInstance().getResponseEntity(HttpStatus.OK, memberChatRoomService.inviteMembers(request.getMemberIds(), request.getChatRoomId()), null);
    }

    @ApiOperation(value="채팅방 떠나기", notes="채팅방의 id를 받아와 토큰의 주인이 해당 채팅방을 나갑니다.")
    @DeleteMapping("chat/leave/{chatRoomId}")
    public ResponseEntity<RootResponse> chatRoomLeave(@AuthenticationPrincipal MemberAuthDTO memberAuthDTO, @PathVariable("chatRoomId") Long chatRoomId) {
        return ResponseUtil.getInstance().getResponseEntity(HttpStatus.OK, memberChatRoomService.leaveChat(memberAuthDTO.getId(), chatRoomId), null);
    }

    @ApiOperation(value="채팅방 목록 조회", notes="자신이 속한 모든 채팅방 내역을 조회합니다.")
    @GetMapping("chat/lists")
    public ResponseEntity<RootResponse> getChatList(@AuthenticationPrincipal MemberAuthDTO memberAuthDTO) {
        return ResponseUtil.getInstance().getResponseEntity(HttpStatus.OK, memberChatRoomService.getChatRoomIdsByMemberId(memberAuthDTO.getId()), null);
    }

    @ApiOperation(value="채팅방 랭킹 조회", notes="채팅방의 id와 max 랭킹을 받아옵니다. 해당 채팅방에서 max 랭킹만큼의 걸음 수 랭킹 리스트를 조회합니다.")
    @PostMapping("chat/ranks")
    public ResponseEntity<RootResponse> getRankList(@AuthenticationPrincipal MemberAuthDTO memberAuthDTO, @RequestBody RankNumChatRoomIdRequest request) {
        return ResponseUtil.getInstance().getResponseEntity(HttpStatus.OK, memberChatRoomService.getRankList(request.getChatRoomId(), memberAuthDTO.getId(), request.getRankNum()), null);
    }
}
