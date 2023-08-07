package com.allianz.erpsystem.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.OneToMany;
import lombok.Data;

import java.util.ArrayList;
import java.util.UUID;

@Data
public class Customer {

    private String customerName;
    private String customerAddress;
    private UUID uuid;

    @OneToMany(targetEntity = Product.class,cascade = CascadeType.ALL)
    private ArrayList<Order> orders;

    private int orderCount;



    public void Product() {
        this.uuid = UUID.randomUUID();
    }
}
