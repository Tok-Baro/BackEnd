package com.api.tokbaro.domain.content.web.dto;

public record ReactionVelocityRes(
        String username, //사용자이름
        double reactionVelocity //사용자 반응속도
) {
}
