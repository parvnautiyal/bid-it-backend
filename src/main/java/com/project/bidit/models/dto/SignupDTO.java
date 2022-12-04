package com.project.bidit.models.dto;

import com.project.bidit.models.Roles;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class SignupDTO {
    private String username;
    private String password;
    private String name;
    private String email;
    private String contact;
    private Set<Roles> roles;
}

