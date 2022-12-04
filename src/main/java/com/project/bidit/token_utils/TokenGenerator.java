package com.project.bidit.token_utils;

import com.project.bidit.models.User;
import com.project.bidit.models.dto.TokenDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Component
public class TokenGenerator {

    public Instant expiry;
    public Instant issuedAt;
    @Autowired
    JwtEncoder accessTokenEncoder;

    @Autowired
    @Qualifier("jwtRefreshTokenEncoder")
    JwtEncoder refreshTokenEncoder;

    private String createAccessToken(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Instant now = Instant.now();
        JwtClaimsSet claimsSet = JwtClaimsSet.builder()
                .issuer("bidit")
                .issuedAt(now)
                .expiresAt(now.plus(1, ChronoUnit.HOURS))
                .subject(user.getId())
                .claim("roles", user.getRoles())
                .build();
        this.expiry = claimsSet.getExpiresAt();
        this.issuedAt = claimsSet.getIssuedAt();
        return accessTokenEncoder.encode(JwtEncoderParameters.from(claimsSet)).getTokenValue();
    }

    public String createRefreshToken(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Instant now = Instant.now();

        JwtClaimsSet claimsSet = JwtClaimsSet.builder()
                .issuer("bidit")
                .issuedAt(now)
                .expiresAt(now.plus(30, ChronoUnit.DAYS))
                .subject(user.getId())
                .build();

        return refreshTokenEncoder.encode(JwtEncoderParameters.from(claimsSet)).getTokenValue();
    }

    public TokenDTO createToken(Authentication authentication) {
        if (!(authentication.getPrincipal() instanceof User user)) {
            throw new BadCredentialsException(MessageFormat.format("principal {0} is not of User type", authentication.getPrincipal().getClass()));
        }
        TokenDTO tokenDTO = new TokenDTO();
        tokenDTO.setUserId(user.getId());
        tokenDTO.setUserName(user.getUsername());
        tokenDTO.setRoles(user.getRoles());
        tokenDTO.setAccessToken(createAccessToken((authentication)));
        tokenDTO.setExpiry(this.expiry);
        tokenDTO.setIssuedAt(this.issuedAt);

        String refreshToken;
        if (authentication.getCredentials() instanceof Jwt jwt) {
            Instant now = Instant.now();
            Instant expiresAt = jwt.getExpiresAt();
            Duration duration = Duration.between(now, expiresAt);
            long daysUntilExpired = duration.toDays();
            if (daysUntilExpired < 7) {
                refreshToken = createRefreshToken(authentication);
            } else refreshToken = jwt.getTokenValue();
        } else {
            refreshToken = createRefreshToken(authentication);
        }
        tokenDTO.setRefreshToken(refreshToken);
        return tokenDTO;
    }
}
