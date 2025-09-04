package com.api.tokbaro.domain.content.service;

import com.api.tokbaro.domain.content.entity.ContentData;
import com.api.tokbaro.domain.content.repository.ContentDataRepository;
import com.api.tokbaro.domain.content.web.dto.ReactionReq;
import com.api.tokbaro.domain.user.entity.User;
import com.api.tokbaro.domain.user.repository.UserRepository;
import com.api.tokbaro.global.exception.CustomException;
import com.api.tokbaro.global.jwt.UserPrincipal;
import com.api.tokbaro.global.response.code.user.UserErrorResponseCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ContentDataServiceImpl implements ContentDataService {

    private final ContentDataRepository contentDataRepository;
    private final UserRepository userRepository;

    @Override
    public void saveReactionVelocity(UserPrincipal userPrincipal, ReactionReq reactionReq) {
        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(()-> new CustomException(UserErrorResponseCode.USER_NOT_FOUND_404));

        ContentData contentData = new ContentData();
        contentData.setUserReactionVelocity(reactionReq.getReactionVelocity());
        contentData.setUser(user);

        contentDataRepository.save(contentData);
    }
}
