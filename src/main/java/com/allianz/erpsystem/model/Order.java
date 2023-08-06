package com.allianz.erpsystem.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.*;

@Data
public class Order {

    private int orderAmount;
    private UUID uuid;
    private int orderNumber;
    private Map<String, Double> kdvSettings = null;


    public Double getKdvSetting(String kdvKey) {
        Map<String, Double> kdvSetting1 = new HashMap<String, Double>();
        kdvSetting1.put("kdv1", 1.0);
        kdvSetting1.put("kdv8", 8.0);
        kdvSetting1.put("kdv18", 18.0);

        this.kdvSettings = kdvSetting1;
        return kdvSetting1.get(kdvKey);
    }

    @OneToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @Enumerated(EnumType.STRING)
    private ApprovalStatementEnum approvalStatement;


    @OneToOne
    @JoinColumn(name = "invoice_id")
    private Invoice invoice;


    @OneToMany(targetEntity = Product.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "product_id")
    private ArrayList<Product> product;


}