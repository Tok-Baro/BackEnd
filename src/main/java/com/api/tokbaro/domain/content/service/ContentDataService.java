package com.api.tokbaro.domain.content.service;


import com.api.tokbaro.domain.apns.web.dto.ApnsRes;
import com.api.tokbaro.domain.apns.web.dto.StateReq;
import com.api.tokbaro.domain.content.web.dto.ReactionReq;
import com.api.tokbaro.domain.content.web.dto.ReactionVelocityRes;

import java.util.List;

public interface ContentDataService {
    void saveReactionVelocity(Long userId, ReactionReq reactionReq);

    List<ReactionVelocityRes> getRanking();

    ApnsRes handlePostureAlert(Long userId, StateReq stateReq);

}
