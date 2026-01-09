package com.app.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Merchant {
    private String publicId;       // matches merchants.public_id
    private String name;           // merchant name
    private String branch;         // branch location
    private String address;        // full address
    private String contactNumber;  // contact phone number
}
