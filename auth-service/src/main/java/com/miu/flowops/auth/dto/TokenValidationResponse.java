package com.miu.flowops.auth.dto;

import com.miu.flowops.auth.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenValidationResponse {
    
    private boolean valid;
    private String userId;
    private String email;
    private Role role;
    private String message;
}
