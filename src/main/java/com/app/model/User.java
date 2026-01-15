package com.app.model;

import com.app.model.enums.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
    @JsonIgnore
    private Role role;
    private Integer roleId;
    private String roleName;   // DB role name
    // Convenient constructor to set all fields except id & password
    public User(String publicId, String username, String email, String firstName, String middleName, String lastName, Role role, Integer roleId) {
        this.publicId = publicId;
        this.username = username;
        this.email = email;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.role = role;
        this.roleId = roleId;
    }

    public User(String publicId, String username, String password, Role role) {
        this.publicId = publicId;
        this.username = username;
        this.password = password;
        this.role = role;
    }
}
