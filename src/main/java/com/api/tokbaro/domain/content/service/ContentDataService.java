package com.api.tokbaro.domain.content.service;


import com.api.tokbaro.domain.content.web.dto.ReactionReq;
import com.api.tokbaro.domain.content.web.dto.ReactionVelocityRes;
import com.api.tokbaro.global.jwt.UserPrincipal;

import java.util.List;

public interface ContentDataService {
    void saveReactionVelocity(Long userId, ReactionReq reactionReq);

    List<ReactionVelocityRes> getRanking();
}
