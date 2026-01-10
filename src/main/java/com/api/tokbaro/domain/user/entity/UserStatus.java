package com.api.tokbaro.domain.user.entity;

//사용자 계정의 상태를 알려주는 Enum 클래스
/*
    ACTIVE -> 활성상태, 사용자가 현재 정상적으로 서비스를 사용할 수 있는 상태
    WITHDRAWN -> 탈퇴상태, 사용자가 서비스에서 탈퇴한 상태, 해당 사용자는 로그인 불가, 개인정보처리방침에 따라 일정 기간 지난 후 데이터 영구삭제
    SUSPENDED -> 정지상태, 관리자에 의해 서비스 사용이 일시적 또는 영구적으로 정지된 상태
 */
public enum UserStatus {
    ACTIVE, WITHDRAWN, SUSPENDED
}
