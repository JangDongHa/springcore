package com.sparta.springcore.service;

import com.sparta.springcore.dto.SignupRequestDto;
import com.sparta.springcore.model.Users;
import com.sparta.springcore.model.UserRoleEnum;
import com.sparta.springcore.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

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
}
