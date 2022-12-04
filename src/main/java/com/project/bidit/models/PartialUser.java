package com.project.bidit.models;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PartialUser {
    private String id;
    private String username;
    private String name;
    private String email;
    private String contact;
}
