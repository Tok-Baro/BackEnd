package com.api.tokbaro.domain.user.service;

import com.api.tokbaro.domain.user.entity.ConsentType;
import com.api.tokbaro.domain.user.web.dto.ConsentDto;
import com.api.tokbaro.global.exception.CustomException;
import com.api.tokbaro.global.response.code.AuthErrorResponseCode;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserConsentValidator implements ConsentValidator {


    @Override
    public void validate(List<ConsentDto> consents) {
        if(consents == null || consents.isEmpty()){
            throw new CustomException(AuthErrorResponseCode.MANDATORY_TERMS_NOT_AGREED_400);
        }

        //이용약관(TERMS) 동의 확인
        boolean termsAgreed = consents.stream()
                .filter(c->c.getConsentType()== ConsentType.TERMS)
                .findFirst()
                .map(ConsentDto::getIsAgreed)
                .orElse(false);

        //개인정보 처리방침(PRIVACY) 동의 확인
        boolean privacyAgreed = consents.stream()
                .filter(c -> c.getConsentType()==ConsentType.PRIVACY)
                .findFirst()
                .map(ConsentDto::getIsAgreed)
                .orElse(false);

        if(!termsAgreed || !privacyAgreed){
            throw new CustomException(AuthErrorResponseCode.MANDATORY_TERMS_NOT_AGREED_400);
        }
    }
}
