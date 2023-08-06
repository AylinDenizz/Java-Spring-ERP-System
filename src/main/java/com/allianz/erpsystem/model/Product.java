package com.allianz.erpsystem.model;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class Product {

    private int productNumber;
    private String productType;
    private String productName;
    private int stockAmount;
    private BigDecimal unitPrice;
    private UUID uuid;



    public void Product() {
        this.uuid = UUID.randomUUID();
    }

}
