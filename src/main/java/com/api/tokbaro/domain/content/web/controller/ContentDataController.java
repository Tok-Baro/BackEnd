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
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ContentDataController {

    private final ContentDataService contentDataService;

    @PostMapping("/users/me/reactions/{userId}")
    public ResponseEntity<SuccessResponse<?>> saveReactionVelocity(
            @PathVariable Long userId,
            @RequestBody ReactionReq reactionReq){
        contentDataService.saveReactionVelocity(userId, reactionReq);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(SuccessResponse.success("데이터 저장에 성공했습니다."));
    }
}
