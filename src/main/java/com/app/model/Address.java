package com.app.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Address {

    private String street;     // house no, street
    private String barangay;
    private String city;
    private String province;
    private String postalCode;
}
