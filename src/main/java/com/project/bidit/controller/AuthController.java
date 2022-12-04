package com.project.bidit.controller;

import com.project.bidit.models.User;
import com.project.bidit.models.dto.LoginDTO;
import com.project.bidit.models.dto.SignupDTO;
import com.project.bidit.token_utils.TokenGenerator;
import com.project.bidit.models.dto.TokenDTO;
import com.project.bidit.service.UserManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.BearerTokenAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:4200", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST})
@Slf4j
public class AuthController {

    @Autowired
    UserManager userManager;

    @Autowired
    TokenGenerator tokenGenerator;

    @Autowired
    DaoAuthenticationProvider daoAuthenticationProvider;

    @Autowired
    @Qualifier("jwtRefreshTokenAuthProvider")
    JwtAuthenticationProvider jwtRefreshTokenAuthProvider;

    @PostMapping("/register")
    public ResponseEntity<TokenDTO> register(@RequestBody SignupDTO signupDTO){
        User user = new User(null,signupDTO.getUsername(),signupDTO.getPassword(),signupDTO.getName(),signupDTO.getEmail(),signupDTO.getContact(),signupDTO.getRoles());
        userManager.createUser(user);
        Authentication authentication = UsernamePasswordAuthenticationToken.authenticated(user, signupDTO.getPassword(), user.getAuthorities());
        return ResponseEntity.ok(tokenGenerator.createToken(authentication));
    }

    @PostMapping("/login")
    public ResponseEntity<TokenDTO> login(@RequestBody LoginDTO loginDto){
        Authentication authentication = daoAuthenticationProvider.authenticate(UsernamePasswordAuthenticationToken.unauthenticated(loginDto.getUsername(),loginDto.getPassword()));
        return ResponseEntity.ok(tokenGenerator.createToken(authentication));
    }

    @PostMapping("/token")
    public ResponseEntity<TokenDTO> token(@RequestBody TokenDTO tokenDTO){
        Authentication authentication = jwtRefreshTokenAuthProvider.authenticate(new BearerTokenAuthenticationToken(tokenDTO.getRefreshToken()));
        return ResponseEntity.ok(tokenGenerator.createToken(authentication));
    }
}
