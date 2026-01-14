package com.app.model;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Customer {

    @JsonIgnore
    private long id;
    private String publicId;
    private String firstName;
    private String middleName;
    private String lastName;
    private String phone;
    private String email;

    private Address address;
}