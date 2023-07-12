package com.example.clubsite.service.fcm;

import com.example.clubsite.dto.request.Payload;
import com.example.clubsite.entity.Chat;
import com.example.clubsite.entity.ChatRoom;
import com.example.clubsite.entity.ClubMember;
import com.example.clubsite.enumType.NotiType;
import com.example.clubsite.exhandler.exception.CheckedException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
public class FCMServiceTest { //mockServer를 통해 fcm 서버의 동작을 모사하는 것으로 테스트 수행

    private MockWebServer mockWebServer;
    private FCMService fcmService;

    @BeforeEach
    public void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        fcmService = new FCMService(new ObjectMapper());
        fcmService.setAPIUrl(mockWebServer.url("/").toString());

        MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    public void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    public void testSendMessageTo() {
        // Given
        String TARGET_TOKEN = "TARGET_TOKEN";
        String TEST_BODY = "Test Body";
        String TEST_TITLE = "Test Title";
        String SENDER_NAME = "senderTestName";
        String TEST_EMAIL = "sender@gmail.com";
        String SENDER_TEST_IMAGE = "senderTestImage";
        String MESSAGE_CONTENT = "hello!";
        Long TEST_ID = 1L;
        // 모의 응답 생성
        MockResponse mockResponse = new MockResponse()
                .setResponseCode(200)
                .setBody("Mock response body");
        // 모의 서버에 응답 설정
        mockWebServer.enqueue(mockResponse);//mock 처럼, 어떤 request가 들어오던, 해당 response를 반환하도록 설정.
        // 테스트에 사용할 Payload, Token, Title, Body 설정
        ClubMember sender = makeClubMember(1L, TEST_EMAIL, SENDER_NAME, SENDER_TEST_IMAGE);
        Payload payload = payloadBuilder(TEST_ID, sender, Chat.builder().message(MESSAGE_CONTENT).chatRoom(ChatRoom.builder().title("chatRoomTitle").build()).build());


        // When
        try {
            fcmService.sendMessageTo(payload, TARGET_TOKEN, TEST_TITLE, TEST_BODY);
        } catch (CheckedException e) {
            throw new RuntimeException(e);
        }
        // 요청이 도착할 때까지 대기
        RecordedRequest recordedRequest = null;
        try {
            recordedRequest = mockWebServer.takeRequest(); //mockWebServer.takeRequest(): mock 서버에 들어온 요청을 가져온다.
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


        // Then
        assertEquals(mockWebServer.url("/"), recordedRequest.getRequestUrl());
        assertEquals("/", recordedRequest.getPath());
        assertEquals("POST", recordedRequest.getMethod());
        assertEquals("application/json; charset=utf-8", recordedRequest.getHeader("Content-Type"));
        assertEquals("{\"validate_only\":false,\"message\":{\"token\":\"" + TARGET_TOKEN + "\",\"topic\":null,\"notification\":{\"title\":\"" + TEST_TITLE + "\",\"body\":\"" + TEST_BODY + "\",\"image\":null},\"data\":null,\"apns\":{\"payload\":{\"notiType\":\"CHAT\",\"chatId\":" + TEST_ID + ",\"nickName\":\"" + SENDER_NAME + "\",\"imagePath\":\"" + SENDER_TEST_IMAGE + "\",\"message\":\"" + MESSAGE_CONTENT + "\",\"linkUrl\":null,\"aps\":{\"alert\":{\"body\":\"" + SENDER_NAME + ": " + MESSAGE_CONTENT + "\"},\"sound\":\"default\",\"badge\":1}}}}}", recordedRequest.getBody().readUtf8());
        //assertEquals("Bearer <ACCESS_TOKEN>", recordedRequest.getHeader("Authorization"));
    }


    @Test
    public void testSendMessageTo_Failure() {
        // Given
        String TARGET_TOKEN = "TARGET_TOKEN";
        String TEST_TITLE = "Test Title";
        String TEST_BODY = "Test Body";
        String SENDER_NAME = "senderTestName";
        String SENDER_TEST_IMAGE = "senderTestImage";
        String MESSAGE_CONTENT = "hello!";
        String TEST_EMAIL = "sender@gmail.com";
        Long TEST_ID = 1L;
        // 모의 응답 생성
        MockResponse mockResponse = new MockResponse()
                .setResponseCode(500)
                .setBody("Internal Server Error");
        // 모의 서버에 응답 설정
        mockWebServer.enqueue(mockResponse);
        // 테스트에 사용할 Payload, Token, Title, Body 설정
        ClubMember sender = makeClubMember(1L, TEST_EMAIL, SENDER_NAME, SENDER_TEST_IMAGE);
        Payload payload = payloadBuilder(TEST_ID, sender, Chat.builder().message(MESSAGE_CONTENT).chatRoom(ChatRoom.builder().title("chatRoomTitle").build()).build());


        // When
        try {
            fcmService.sendMessageTo(payload, TARGET_TOKEN, TEST_TITLE, TEST_BODY);
        } catch (CheckedException e) {
            throw new RuntimeException(e);
        }
        // 요청이 도착할 때까지 대기
        RecordedRequest recordedRequest = null;
        try {
            recordedRequest = mockWebServer.takeRequest();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // Then
        assertEquals(mockWebServer.url("/"), recordedRequest.getRequestUrl());
        assertEquals("/", recordedRequest.getPath());
        assertEquals("POST", recordedRequest.getMethod());
        assertEquals("application/json; charset=utf-8", recordedRequest.getHeader("Content-Type"));
        assertEquals("{\"validate_only\":false,\"message\":{\"token\":\"" + TARGET_TOKEN + "\",\"topic\":null,\"notification\":{\"title\":\"" + TEST_TITLE + "\",\"body\":\"" + TEST_BODY + "\",\"image\":null},\"data\":null,\"apns\":{\"payload\":{\"notiType\":\"CHAT\",\"chatId\":" + TEST_ID + ",\"nickName\":\"" + SENDER_NAME + "\",\"imagePath\":\"" + SENDER_TEST_IMAGE + "\",\"message\":\"" + MESSAGE_CONTENT + "\",\"linkUrl\":null,\"aps\":{\"alert\":{\"body\":\"" + SENDER_NAME + ": " + MESSAGE_CONTENT + "\"},\"sound\":\"default\",\"badge\":1}}}}}", recordedRequest.getBody().readUtf8());
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
