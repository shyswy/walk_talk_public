package com.example.clubsite.service.notyet;

import com.example.clubsite.dto.request.Payload;
import com.example.clubsite.entity.Chat;
import com.example.clubsite.entity.ChatRoom;
import com.example.clubsite.entity.ClubMember;
import com.example.clubsite.enumType.NotiType;
import com.example.clubsite.exhandler.exception.CheckedException;
import com.example.clubsite.exhandler.exception.CustomException;
import com.example.clubsite.service.fcm.FCMService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class FCMServiceWithOutClientTest {


    @InjectMocks
    @Spy
    private FCMService fcmService;

    @Spy
    private ObjectMapper objectMapper;


    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("FCM 메시지 전송 성공")
    void testSendMessageTo_Success() {
        ClubMember sender = makeClubMember(1L, "sender@gmail.com", "senderTestName", "senderTestImage"); //메세지

        // given
        Payload payload = payloadBuilder(1L, sender, Chat.builder().message("hello!").chatRoom(ChatRoom.builder().title("chatRoomTitle").build()).build());
        String targetToken = "TARGET_TOKEN";
        String title = "Test Title";
        String body = "Test Body";

        doNothing().when(fcmService).asyncRequestAndResponse(any(), any(), "", "", "");

        // when
        try {
            fcmService.sendMessageTo(payload, targetToken, title, body);
        } catch (CheckedException e) {
            throw new RuntimeException(e);
        }

        // then
        verify(fcmService, times(1)).asyncRequestAndResponse(any(), any(), "", "", "");
    }

    @Test
    @DisplayName("FCM 메시지 전송 실패 - 예외 발생")
    void testSendMessageTo_Failure_Exception() {
        // given
        Payload payload = new Payload();
        String targetToken = "TARGET_TOKEN";
        String title = "Test Title";
        String body = "Test Body";

        doThrow(new RuntimeException("Failed to send message")).when(fcmService).asyncRequestAndResponse(any(), any(), "", "", "");

        // when & then
        assertThrows(CustomException.class, () -> fcmService.sendMessageTo(payload, targetToken, title, body));
    }

    private static ClubMember makeClubMember(Long id, String email, String name, String imageName) {
        ClubMember registeredClubMember = ClubMember.builder()
                .id(id)
                .email(email)
                .name(name)
                .profileFileName(imageName)
                .build();
        return registeredClubMember;
    }

    private Payload payloadBuilder(Long chatRoomId, ClubMember clubMember, Chat chat) {
        return Payload.builder()
                .chatId(chatRoomId)
                .nickName(clubMember.getName())
                .message(chat.getMessage())
                .imagePath(clubMember.getProfileFileName())
                .notiType(NotiType.CHAT.name())
                .linkUrl("not yet")
                .build();
    }
}
