package com.project.bidit.token_utils;

import com.project.bidit.models.User;
import com.project.bidit.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class JwtToUserConverter implements Converter<Jwt, UsernamePasswordAuthenticationToken> {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UsernamePasswordAuthenticationToken convert(Jwt jwt) {
        String id = jwt.getSubject();
        log.info("ROLES IN CLAIMS -> "+jwt.getClaim("roles"));
        log.info("CLAIMS-> "+jwt.getClaims().toString());
        User user = userRepository.findById(id).get();

        return new UsernamePasswordAuthenticationToken(user,jwt,user.getAuthorities());
    }
}
