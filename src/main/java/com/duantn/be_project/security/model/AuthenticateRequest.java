package com.duantn.be_project.security.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthenticateRequest {
    String email;
    String password;
    Boolean isGoogleLogin;
}
