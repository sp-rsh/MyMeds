package org.example.mymedsbackend.user;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserAdminUpdateRequest {
    private String username;
    private String email;
    private String role;
    private String password;

    private String discordID;

}
