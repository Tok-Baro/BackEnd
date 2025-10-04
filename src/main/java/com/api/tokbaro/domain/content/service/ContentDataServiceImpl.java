package com.api.tokbaro.domain.content.service;

import com.api.tokbaro.domain.apns.service.ApnsService;
import com.api.tokbaro.domain.apns.web.dto.ApnsRes;
import com.api.tokbaro.domain.apns.web.dto.StateReq;
import com.api.tokbaro.domain.content.entity.ContentData;
import com.api.tokbaro.domain.content.repository.ContentDataRepository;
import com.api.tokbaro.domain.content.web.dto.ReactionReq;
import com.api.tokbaro.domain.content.web.dto.ReactionVelocityRes;
import com.api.tokbaro.domain.user.entity.User;
import com.api.tokbaro.domain.user.repository.UserRepository;
import com.api.tokbaro.global.exception.CustomException;
import com.api.tokbaro.global.response.code.user.UserErrorResponseCode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContentDataServiceImpl implements ContentDataService {

    private final ContentDataRepository contentDataRepository;
    private final UserRepository userRepository;
    private final ApnsService apnsService;

    @Override
    @Transactional
    public void saveReactionVelocity(Long userId, ReactionReq reactionReq) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(UserErrorResponseCode.USER_NOT_FOUND_404));

        //userID로 ContentData를 찾고 없다면 새로 생성한다.
        ContentData contentData = contentDataRepository.findByUserId(userId)
                .orElseGet(() -> ContentData.builder().user(user).build());

        //기록 업데이트
        contentData.updateReactionVelocity(reactionReq.getReactionVelocity());


        contentDataRepository.save(contentData);
    }

    @Override
    public List<ReactionVelocityRes> getRanking() {
        Pageable top10 = PageRequest.of(0, 10);
        return contentDataRepository.findAllByOrderByUserReactionVelocityAsc(top10)
                .getContent()
                .stream()
                .map(contentData -> new ReactionVelocityRes(
                        contentData.getUser().getUsername(),
                        contentData.getUserReactionVelocity()
                ))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ApnsRes handlePostureAlert(Long userId, StateReq stateReq) {
        ContentData contentData = contentDataRepository.findByUserId(userId)
                .orElseThrow(()->new CustomException(UserErrorResponseCode.CONTENT_DATA_NOT_FOUND_404));

        //contentData.setAlertCount(contentData.getAlertCount() + 1);
        contentData.increaseAlertCount();

        return apnsService.sendPostureAlert(stateReq);
    }
}
