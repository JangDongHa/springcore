package com.sparta.springcore.model;

public enum UserRoleEnum {
    USER(Authority.USER), // 사용자 권한
    ADMIN(Authority.ADMIN); // 관리자 권한

    private final String authority;

    UserRoleEnum(String authority){ // 여기서 받아온 값(로그인한 유저 auth)을 저장
        this.authority = authority;
    }
    private static final String PREFIX_ROLE_NAME = "ROLE_";

    public String getAuthority(){
        return this.authority;
    }
    public static class Authority {
        public static final String USER = "ROLE_USER";
        public static final String ADMIN = "ROLE_ADMIN";
    }
}