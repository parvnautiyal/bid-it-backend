package com.project.bidit.models.dto;

import com.project.bidit.models.PartialUser;
import com.project.bidit.models.Status;
import com.project.bidit.models.User;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
public class ProductDTO {
    private String name;
    private String description;
    private String img;
    private String duration;
    private LocalDateTime start;
    private LocalDateTime end;
    private Integer amount;
    private PartialUser user;
    private Status status;
    private Map<String, Map<String, String>> bids;
    private User buyer;
}
