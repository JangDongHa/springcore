package com.sparta.springcore.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.springcore.dto.KakaoUserInfoDto;
import com.sparta.springcore.dto.SignupRequestDto;
import com.sparta.springcore.model.UserRoleEnum;
import com.sparta.springcore.model.Users;
import com.sparta.springcore.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
public class UserService {
    // Bean 사용
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encodePassword;
    private static final String ADMIN_TOKEN = "AAABnv/xRVklrnYxKZ0aHgTBcXukeZygoC";

    @Autowired
    public UserService(UserRepository userRepository, BCryptPasswordEncoder encodePassword) {
        this.userRepository = userRepository;
        this.encodePassword = encodePassword;
    }

    public void registerUser(SignupRequestDto requestDto) {
        String nickname = requestDto.getNickname();
        String username = requestDto.getUsername();
        String password = encodePassword.encode(requestDto.getPassword());
        // 회원 ID 중복 확인
        Optional<Users> found = userRepository.findByUsername(username);
        if (found.isPresent()) {
            throw new IllegalArgumentException("중복된 사용자 ID 가 존재합니다.");
        }

        String email = requestDto.getEmail();
        // 사용자 ROLE 확인
        UserRoleEnum role = UserRoleEnum.USER;
        
        // 관리자인지 확인
        if (requestDto.isAdmin()) { // 관리자가 TRUE이면
            if (!requestDto.getAdminToken().equals(ADMIN_TOKEN)) { // 토큰 확인
                throw new IllegalArgumentException("관리자 암호가 틀려 등록이 불가능합니다.");
            }
            role = UserRoleEnum.ADMIN;
        }

        Users user = new Users(nickname, username, password, email, role);
        userRepository.save(user);
    }

    private String getAccessToken(String code) throws JsonProcessingException {
        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP Body 생성
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", "5bc9f50db85bb799e178458056d105e3");
        body.add("redirect_uri", "http://jdh3341.shop/user/kakao/callback");
        body.add("code", code);

        // HTTP 요청 보내기
        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(body, headers);
        JsonNode jsonNode = getKakaoApiResult(kakaoTokenRequest, "https://kauth.kakao.com/oauth/token"); // 엑세스 토큰 API 호출

        return jsonNode.get("access_token").asText(); // accessToken
    }

    public JsonNode getKakaoApiResult(HttpEntity<MultiValueMap<String, String>> httpEntity, String sendingUrl) throws JsonProcessingException {
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response = rt.exchange(
                sendingUrl,
                HttpMethod.POST,
                httpEntity,
                String.class
        ); // response : json 타입으로 되어있음

        // HTTP 응답 (JSON) -> 액세스 토큰 파싱
        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readTree(responseBody);
    }

    private KakaoUserInfoDto callKakaoApi(String accessToken) throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        // HTTP Header 생성
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP 요청 보내기
        HttpEntity<MultiValueMap<String, String>> kakaoUserInfoRequest = new HttpEntity<>(headers);
        JsonNode jsonNode = getKakaoApiResult(kakaoUserInfoRequest, "https://kapi.kakao.com/v2/user/me"); // 엑세스 토큰을 카카오에게 주면서 회원정보 결과를 받는 API

        // Dto : id, nickname, email
        Long id = jsonNode.get("id").asLong();
        String nickname = jsonNode.get("properties").get("nickname").asText();
        String email = jsonNode.get("kakao_account").get("email").asText();

        KakaoUserInfoDto kakaoUserInfoDto = new KakaoUserInfoDto(id, nickname, email);

        System.out.println("Kakao User Info : " + id + ", " + nickname + ", " + email);
        return kakaoUserInfoDto;
    }


    public void kakaoLogin(String code) throws JsonProcessingException {
    // 1. "인가 코드"로 "액세스 토큰" 요청
        String accessCode = getAccessToken(code);
    // 2. 토큰으로 카카오 API 호출
        KakaoUserInfoDto userInfo = callKakaoApi(accessCode);

    }
}
