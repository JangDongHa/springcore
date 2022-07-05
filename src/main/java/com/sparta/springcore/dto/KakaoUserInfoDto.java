package com.sparta.springcore.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor // 모든 Args 를 받은 생성자 생성
@NoArgsConstructor // 아무것도 없는 Args 를 받은 생성자 생성
public class KakaoUserInfoDto {
    private Long id;
    private String nickname;
    private String email;
}
