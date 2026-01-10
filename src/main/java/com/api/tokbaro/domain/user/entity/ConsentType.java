package com.api.tokbaro.domain.user.entity;

//동의 항목의 종류를 정의하는 Enum클래스
public enum ConsentType {
    TERMS, //이용약관
    PRIVACY, //개인정보 처리방침
    SENSITIVE_DATA, //민감정보 수집 (자세 정보 등등...)
    MARKETING, //마케팅 정보 수신
    AD_TRACKING //광고 추적 동의
}
