package com.api.tokbaro.domain.content.service;

import com.api.tokbaro.domain.content.entity.ContentData;
import com.api.tokbaro.domain.content.repository.ContentDataRepository;
import com.api.tokbaro.domain.content.web.dto.ReactionReq;
import com.api.tokbaro.domain.content.web.dto.ReactionVelocityRes;
import com.api.tokbaro.domain.user.entity.User;
import com.api.tokbaro.domain.user.repository.UserRepository;
import com.api.tokbaro.global.exception.CustomException;
import com.api.tokbaro.global.jwt.UserPrincipal;
import com.api.tokbaro.global.response.code.user.UserErrorResponseCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContentDataServiceImpl implements ContentDataService {

    private final ContentDataRepository contentDataRepository;
    private final UserRepository userRepository;

    @Override
    public void saveReactionVelocity(Long userId, ReactionReq reactionReq) {
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new CustomException(UserErrorResponseCode.USER_NOT_FOUND_404));

        //사용자 ID를 사용해 반응속도 데이터 조회
        Optional<ContentData> optionalContentData = contentDataRepository.findByUserId(userId);

        //기존 데이터가 있는지 확인
        if(optionalContentData.isPresent()) {
            //기존 데이터 있다면 가져옴
            ContentData contentData = optionalContentData.get();

            //반응속도 비교(이전 기록, 현재 기록)
            if(contentData.getUserReactionVelocity() > reactionReq.getReactionVelocity()) {
                contentData.setUserReactionVelocity(reactionReq.getReactionVelocity());
                contentDataRepository.save(contentData);
            }
        }
        else{ //기록이 없는 경우
            ContentData contentData = new ContentData();
            contentData.setUserReactionVelocity(reactionReq.getReactionVelocity());
            contentData.setUser(user);

            contentDataRepository.save(contentData);
        }
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
}
