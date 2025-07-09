package com.personal.spring_questly.service.impl;

import com.personal.spring_questly.exception.CustomException.NotFoundException;
import com.personal.spring_questly.model.User;
import com.personal.spring_questly.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.info("loading user details...");

        User foundUser = userRepository.findByEmail(email).orElseThrow(
                () -> new NotFoundException("User not found", null)
        );

        Set<GrantedAuthority> authorities = new HashSet<>();

        if (foundUser.isQuester()) {
            authorities.add(new SimpleGrantedAuthority("QUESTER"));
        }

        if (foundUser.isQuester()) {
            authorities.add(new SimpleGrantedAuthority("REQUESTER"));
        }

        return org.springframework.security.core.userdetails.User.builder()
                                                                 .username(foundUser.getEmail())
                                                                 .password("")
                                                                 .authorities(authorities)
                                                                 .build();
    }
}
