package com.api.tokbaro.domain.apns.web.controller;

import com.api.tokbaro.domain.apns.service.ApnsService;
import com.api.tokbaro.domain.apns.web.dto.ApnsReq;
import com.api.tokbaro.domain.apns.web.dto.ApnsRes;
import com.api.tokbaro.domain.apns.web.dto.StateReq;
import com.api.tokbaro.domain.content.service.ContentDataService;
import com.api.tokbaro.domain.content.service.ContentDataServiceImpl;
import com.api.tokbaro.domain.user.service.UserService;
import com.api.tokbaro.global.jwt.UserPrincipal;
import com.api.tokbaro.global.response.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/apns")
@RequiredArgsConstructor
public class ApnsController {

    private final ApnsService apnsService;
    private final ContentDataService contentDataService;

    @PostMapping("/register")
    public ResponseEntity<SuccessResponse<?>> register(@RequestBody ApnsReq apnsReq) {
        apnsService.registerActivity(apnsReq);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(SuccessResponse.empty());
    }

    @PostMapping("/unregister")
    public ResponseEntity<SuccessResponse<?>> unregister(@RequestBody ApnsReq apnsReq) {
        apnsService.unregisterActivity(apnsReq);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(SuccessResponse.empty());
    }

    @PostMapping("/state")
    public ResponseEntity<SuccessResponse<?>> updateState(@RequestBody StateReq stateReq) {
        ApnsRes apnsRes = apnsService.updateActivityState(stateReq);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(SuccessResponse.ok(apnsRes));
    }

    @PostMapping("/posture-alert")
    public ResponseEntity<SuccessResponse<?>> postureAlert(
            @RequestBody StateReq stateReq,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        //ApnsRes apnsRes = apnsService.sendPostureAlert(stateReq);
        ApnsRes apnsRes = contentDataService.handlePostureAlert(userPrincipal.getId(), stateReq);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(SuccessResponse.ok(apnsRes));
    }
}
