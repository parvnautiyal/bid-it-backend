package com.project.bidit.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Map;

@Document(collection = "Products")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    private String id;
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
