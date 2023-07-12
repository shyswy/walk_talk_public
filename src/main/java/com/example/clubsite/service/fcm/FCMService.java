package com.example.clubsite.service.fcm;

import com.example.clubsite.dto.fcm.FCMMessage;
import com.example.clubsite.dto.request.Payload;
import com.example.clubsite.enumType.NotiType;
import com.example.clubsite.exhandler.errorcode.CheckedErrorResponseCode;
import com.example.clubsite.exhandler.errorcode.CommonErrorResponseCode;
import com.example.clubsite.exhandler.exception.CheckedException;
import com.example.clubsite.exhandler.exception.RestApiException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FCMService {
    public static final String FIREBASE_CONFIGPATH = "walktalk-840b4-firebase-adminsdk-8u270-5b4f5a8d19.json";
    private static final String BEARER = "Bearer ";
    public static final String DEFAULT = "default";
    public static final String GOOGLE_AUTH_API_URL = "https://www.googleapis.com/auth/cloud-platform";
    public static final String JSON_UTF_8 = "application/json; UTF-8";
    public static final String APPLICATION_JSON_CHARSET_UTF_8 = "application/json; charset=utf-8";

    @Value("${FCM_URL}")
    private String API_URL;
    private final ObjectMapper objectMapper;

    public void sendMessageTo(Payload payload, String targetToken, String title, String body) throws CheckedException {
        OkHttpClient client = new OkHttpClient();
        String message = makeMessage(payload, targetToken, title, body);
        RequestBody requestBody = RequestBody.create(message, MediaType.get(APPLICATION_JSON_CHARSET_UTF_8));
        Request request = null;
        try {
            request = getRequest(requestBody);
        } catch (IOException e) {
            throw new RestApiException(CommonErrorResponseCode.IO_ERROR);
        }
        asyncRequestAndResponse(client, request, payload.getNickName(), payload.getMessage(), targetToken);
    }

    @Transactional(readOnly = true)//ok
    public String getAccessToken() throws IOException {
        String firebaseConfigPath = FIREBASE_CONFIGPATH;

        GoogleCredentials googleCredentials = GoogleCredentials
                .fromStream(new ClassPathResource(firebaseConfigPath).getInputStream())
                .createScoped(List.of(GOOGLE_AUTH_API_URL));

        googleCredentials.refreshIfExpired();
        return googleCredentials.getAccessToken().getTokenValue();
    }

    //async, non-blocking
    //비동기 처리로 인하여 해당 메소드는 호출자와 별개로 수행. 따라서 에러 처리도 별도로 수행된다.
    public void asyncRequestAndResponse(OkHttpClient client, Request request, String senderName, String message, String targetFcmToken) { //비동기, 호출자에 전파 x
        client.newCall(request).enqueue(new Callback() { //각 콜백(리턴 response) 를 큐에 쌓아둔다.
            @Override
            public void onFailure(Call call, IOException e) { //response 가 반환되지 않음.
                log.error("FCM PUSH FAILURE  \n" + e.toString());
                log.error("sender: {}, targetToken: {}, message: {}", senderName, targetFcmToken, message);
            }

            @Override
            public void onResponse(Call call, Response response) {
                try (ResponseBody responseBody = response.body()) {//try with resource: responseBody를 자동으로 닫아준다.
                    if (!response.isSuccessful()) { //http 상태코드 체크
                        log.error("FCM PUSH NOT SUCCESSFUL");
                        log.error("sender: {}, targetToken: {}, message: {}", senderName, targetFcmToken, message);
                        log.error("responseBody     \n{}", responseBody.string()); //응답 데이터 가져오기 ( .toString은 객체 자체를 변환하여 가져온다. )
                    }
//                    log.info("responseBody     \n{}", responseBody.string()); //응답 데이터 가져오기 ( .toString은 객체 자체를 변환하여 가져온다. )
                } catch (IOException e) {
                    log.error("FCM PUSH FAILURE  \n" + e);
                    throw new RestApiException(CommonErrorResponseCode.IO_ERROR);
                }
                response.body().close();
            }
        });
    }

    public String makeMessage(Payload payload, String targetToken, String title, String body) { //전체 광고 등등. 전체 포맷을 보내주면 body만 처리하게.
        FCMMessage fcmMessage = FCMMessage.builder()
                .message(FCMMessage.Message.builder()
                        .token(targetToken)
                        .apns(buildApns(payload))
                        .notification(buildFcmNotification(title, body))
                        .build()
                )
                .validate_only(false)
                .build();
        try {
            return objectMapper.writeValueAsString(fcmMessage);
        } catch (JsonProcessingException e) {//ioException 잡아서 변환
            throw new RestApiException(CommonErrorResponseCode.IO_ERROR, e.getMessage());
        }

    }

    public void setAPIUrl(String mockServerUrl) {//for test
        this.API_URL = mockServerUrl;
    }

    @NotNull
    private Request getRequest(RequestBody requestBody) throws IOException {
        return new Request.Builder()
                .url(API_URL)
                .post(requestBody)
                .addHeader(HttpHeaders.AUTHORIZATION, BEARER + getAccessToken())
                .addHeader(HttpHeaders.CONTENT_TYPE, JSON_UTF_8)
                .build();
    }

    private static FCMMessage.Notification buildFcmNotification(String title, String body) {
        return FCMMessage.Notification.builder()
                .title(title)
                .body(body)
                .image(null)
                .build();
    }

    private static FCMMessage.Apns buildApns(Payload payload) {
        return FCMMessage.Apns.builder()
                .payload(FCMMessage.Payload.builder()
                        .aps(FCMMessage.Aps.builder()
                                .alert(FCMMessage.Alert.builder()
                                        .body(payload.getNickName() + ": " + payload.getMessage())
                                        .build())
                                .sound(DEFAULT)
                                .badge(1)
                                .build()
                        )
                        .notiType(NotiType.CHAT.getValue())
                        .chatId(payload.getChatId())
                        .nickName(payload.getNickName())
                        .imagePath(payload.getImagePath())
                        .message(payload.getMessage())
                        .build()
                )
                .build();
    }
}
