package com.api.tokbaro.domain.content.web.controller;

import com.api.tokbaro.domain.content.service.ContentDataService;
import com.api.tokbaro.domain.content.service.ContentDataServiceImpl;
import com.api.tokbaro.domain.content.web.dto.ReactionReq;
import com.api.tokbaro.domain.content.web.dto.ReactionVelocityRes;
import com.api.tokbaro.domain.user.entity.User;
import com.api.tokbaro.global.jwt.UserPrincipal;
import com.api.tokbaro.global.response.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ContentDataController {

    private final ContentDataService contentDataService;

    //이거 나중에 JWT방식으로 갈아야함 통신 테스트하려고
    //반응속도 저장
    @PostMapping("/users/me/reactions")
    public ResponseEntity<SuccessResponse<?>> saveReactionVelocity(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody ReactionReq reactionReq){
        contentDataService.saveReactionVelocity(userPrincipal.getId(), reactionReq);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(SuccessResponse.success("데이터 저장에 성공했습니다."));
    }

    //반응속도 랭킹 반환
    @GetMapping("/users/me/reactions")
    public ResponseEntity<SuccessResponse<?>> getRanking(){
        List<ReactionVelocityRes> rankingList = contentDataService.getRanking();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(SuccessResponse.ok(rankingList));
    }
}
