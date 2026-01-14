package com.app.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    private long id;
    private String name;
    private String description;
    private int categoryId;
    private int sizeId;
    private double price;
    private boolean availability;

}
