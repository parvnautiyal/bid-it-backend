package com.project.bidit.models.dto;

import com.project.bidit.models.Roles;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TokenDTO {
    private String userId;
    private String accessToken;
    private String refreshToken;
    private String userName;
    private Instant expiry;
    private Instant issuedAt;
    private Set<Roles> roles;
}
