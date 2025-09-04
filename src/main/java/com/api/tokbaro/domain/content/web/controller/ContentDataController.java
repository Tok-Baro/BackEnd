package com.api.tokbaro.domain.content.web.controller;

import com.api.tokbaro.domain.content.service.ContentDataService;
import com.api.tokbaro.domain.content.service.ContentDataServiceImpl;
import com.api.tokbaro.domain.content.web.dto.ReactionReq;
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
@RequiredArgsConstructor
@RequestMapping("/api")
public class ContentDataController {

    private final ContentDataService contentDataService;

    @PostMapping("/users/me/reactions")
    public ResponseEntity<SuccessResponse<?>> saveReactionVelocity(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody ReactionReq reactionReq){
        contentDataService.saveReactionVelocity(userPrincipal, reactionReq);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(SuccessResponse.success("데이터 저장에 성공했습니다."));
    }
}
