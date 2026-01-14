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

    private String publicId;
    private String name;
    private String branch;         // branch location
    private String address;
    private String contactNumber;

}
