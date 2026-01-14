package com.app.model;

import com.app.model.enums.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @JsonIgnore
    private int id;

    private String publicId;
    private String username;
    private String email;
    private String firstName;
    private String middleName;
    private String lastName;

    @JsonIgnore
    private String password;
    private Role role;
    private Integer roleId;
    public User( String publicId, String username, String password, Role role){
        this.publicId = publicId;
        this.username = username;
        this.password = password;
        this.role = role;
    }
}
