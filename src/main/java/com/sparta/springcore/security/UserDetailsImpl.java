package com.sparta.springcore.security;

import com.sparta.springcore.model.UserRoleEnum;
import com.sparta.springcore.model.Users;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

// 로그인된 회원정보
public class UserDetailsImpl implements UserDetails {

    private final Users user;

    public UserDetailsImpl(Users user) {
        this.user = user;
    }

    public Users getUser() {
        return user;
    }


    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        UserRoleEnum userRoleEnum = user.getRole();
        String authority = userRoleEnum.getAuthority();

        SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(authority); // GrantedAuthority 확인해보면 SimpleGrantedAuthority 가 나오기 때문에 해당 객체를 만들어주고 authorites 객체에 add 해주고 뿌려줌
        Collection<GrantedAuthority> authorities = new ArrayList<>(); // GrantedAuthority 인터페이스에서 정의해놓은 클래스를 확인(Goto -> Implementation)
        authorities.add(simpleGrantedAuthority);
        return authorities;
    }
}