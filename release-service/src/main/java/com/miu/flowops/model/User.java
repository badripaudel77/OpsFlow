package com.miu.flowops.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
public class User {
    @Id
    private String id;

    private String email;

    private String username;

    private String fullName;

    @JsonIgnore
    private String password;

    @JsonIgnore
    private boolean verified = true;

    private String avatarUrl;

    private Set<Role> roles;
}
