package com.api.tokbaro.domain.content.service;


import com.api.tokbaro.domain.content.web.dto.ReactionReq;
import com.api.tokbaro.global.jwt.UserPrincipal;

public interface ContentDataService {
    void saveReactionVelocity(UserPrincipal userPrincipal, ReactionReq reactionReq);
}
