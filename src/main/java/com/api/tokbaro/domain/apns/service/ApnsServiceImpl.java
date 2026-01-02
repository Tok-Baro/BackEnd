package com.api.tokbaro.domain.apns.service;

import com.api.tokbaro.domain.apns.web.dto.ApnsReq;
import com.api.tokbaro.domain.apns.web.dto.ApnsRes;
import com.api.tokbaro.domain.apns.web.dto.StateReq;
import com.eatthepath.pushy.apns.*;
import com.eatthepath.pushy.apns.util.TokenUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApnsServiceImpl implements ApnsService {

    /*
        일반 HashMap 사용시 다른 스레드가 접근하여 데이터가 깨지거나 의도치 않은 오류 발생 가능 -> '경합 조건'
        ConcurrentHashMap은 멀티 스레드 환경에서 안전하게 데이터를 공유하고 수정할 수 있도록 해주는 클래스
     */
    private final ApnsClient apnsClient;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Map<String, String> activityTokens = new ConcurrentHashMap<>();

    //SOC 관심사의 분리로 ApnsConfig에 두지않고 여기서 관리
    //Config에는 연결에 직결하는 변수들만 넣어둔다.
    @Value("${apns.bundle-id}")
    private String bundleId;

    @Override //액티비티 등록
    public void registerActivity(ApnsReq apnsReq) {
        activityTokens.put(apnsReq.getActivityId(), apnsReq.getToken());
        log.info("Registered activityId : {} with Token : {}", apnsReq.getActivityId(), apnsReq.getToken());
    }

    @Override //액티비티 해제
    public void unregisterActivity(ApnsReq apnsReq) {
        activityTokens.remove(apnsReq.getActivityId());
        log.info("Unregistered activityId : {}", apnsReq.getActivityId());
    }

    @Override
    public ApnsRes updateActivityState(StateReq stateReq) {
        String token = activityTokens.get(stateReq.getActivityId());
        if (token == null) {
            String errorMessage = "토큰을 찾을 수 없음 (Activity ID: " + stateReq.getActivityId() + ")";
            log.warn(errorMessage);
            return new ApnsRes(404, errorMessage);
        }
        Map<String, Object> contentState = createContentState(stateReq);
        return sendNotification(token, contentState, null, 10);
    }
 // priority = 10 (즉시 전송) , 5 = (전력 효율적 전송) 배터리 관리 때문에 있는듯
    @Override
    public ApnsRes sendPostureAlert(StateReq stateReq) {
        String token = activityTokens.get(stateReq.getActivityId());
        if (token == null) {
            String errorMessage = "토큰을 찾을 수 없음 (Activity ID: " + stateReq.getActivityId() + ")";
            log.warn(errorMessage);
            return new ApnsRes(404, errorMessage);
        }

        Map<String, Object> contentState = createContentState(stateReq);
        Map<String, Object> alert = new HashMap<>();
        alert.put("title", stateReq.getMessageTitle() != null ? stateReq.getMessageTitle() : "똑:바로");
        alert.put("body", stateReq.getMessageBody() != null ? stateReq.getMessageBody() : "자세가 안좋아요, 자세를 바르게 고쳐주세요");
        alert.put("sound", "default");

        return sendNotification(token, contentState, alert, 10);
    }

    //APNs 페이로드의 content-state 만드는 역할
    private Map<String, Object> createContentState(StateReq stateReq) {
        Map<String, Object> contentState = new HashMap<>();
        contentState.put("pitch", stateReq.getPitch());
        contentState.put("roll", stateReq.getRoll());
        contentState.put("yaw", stateReq.getYaw());
        contentState.put("ax", stateReq.getAx());
        contentState.put("ay", stateReq.getAy());
        contentState.put("az", stateReq.getAz());
        contentState.put("rx", stateReq.getRx());
        contentState.put("ry", stateReq.getRy());
        contentState.put("rz", stateReq.getRz());
        contentState.put("gx", stateReq.getGx());
        contentState.put("gy", stateReq.getGy());
        contentState.put("gz", stateReq.getGz());
        return contentState;
    }

    private ApnsRes sendNotification(String token, Map<String, Object> contentState, Map<String, Object> alert, int priority) {
        String topic = bundleId + ".push-type.liveactivity";

        Map<String, Object> aps = new HashMap<>();
        aps.put("timestamp", Instant.now().getEpochSecond());
        aps.put("event", "update");
        aps.put("content-state", contentState);
        if (alert != null) {
            aps.put("alert", alert);
        }

        String payload;
        try {
            payload = objectMapper.writeValueAsString(Map.of("aps", aps));
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize APNs payload", e);
            return new ApnsRes(500, "페이로드 직렬화 실패 " + e.getMessage());
        }

        //log.info("전달된 페이로드: {}", payload);
        final com.eatthepath.pushy.apns.DeliveryPriority deliveryPriority = priority == 5 ?
                com.eatthepath.pushy.apns.DeliveryPriority.CONSERVE_POWER :
                com.eatthepath.pushy.apns.DeliveryPriority.IMMEDIATE;

// ApnsPushNotification 인터페이스를 직접 구현하는 익명 클래스 사용
        ApnsPushNotification notification = new ApnsPushNotification() {
            @Override
            public String getToken() {
                return TokenUtil.sanitizeTokenString(token);
            }

            @Override
            public String getPayload() {
                return payload;
            }

            @Override
            public String getTopic() {
                return topic;
            }

            @Override
            public Instant getExpiration() {
                // 만료 시간을 null로 명시적으로 반환
                return Instant.now().plus(10, ChronoUnit.MINUTES);
            }

            @Override
            public DeliveryPriority getPriority() {
                return deliveryPriority;
            }

            @Override
            public String getCollapseId() {
                return null;
            }

            @Override
            public PushType getPushType() {
                return PushType.LIVE_ACTIVITY;
            }

            @Override
            public UUID getApnsId() {
                return null;
            }
        };

        ApnsRes result;
        try {
            final PushNotificationResponse<ApnsPushNotification> response =
                    apnsClient.sendNotification(notification).get();

            if (response.isAccepted()) {
                //log.info("Push notification sent successfully to token: {}", token);
                result =  new ApnsRes(200, "성공");
            } else {
                log.error("Push notification failed for token: {}. Reason: {}", token, response.getRejectionReason());
                response.getTokenInvalidationTimestamp().ifPresent(timestamp ->
                        log.error("Token was invalidated at {}", timestamp)
                );
                result = new ApnsRes(400, "APNs 거절 사유:" + response.getRejectionReason());
            }
        } catch (InterruptedException | ExecutionException e) {
            log.error("Failed to send push notification", e);
            Thread.currentThread().interrupt();
            result =  new ApnsRes(500, "알림 전송 실패: " + e.getMessage());
        }
        return result;
    }
}