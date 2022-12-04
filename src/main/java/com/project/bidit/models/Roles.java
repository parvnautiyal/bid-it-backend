package com.project.bidit.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Roles {

    private String roleDescription;
    private Role roleName;
}

enum Role {
    ROLE_ADMIN,
    ROLE_USER,
    ROLE_OWNER
}
