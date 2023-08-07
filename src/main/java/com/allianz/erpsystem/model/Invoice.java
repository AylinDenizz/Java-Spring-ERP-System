package com.allianz.erpsystem.model;

import com.allianz.erpsystem.database.entity.OrderEntity;
import com.allianz.erpsystem.database.entity.ProductEntity;
import jakarta.persistence.*;
import lombok.Data;
import org.aspectj.weaver.ast.Or;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
public class Invoice {
    private int unitPrice;
    private int TotalPrice;
    private int kdvAddedTotalPrice;
    private UUID uuid;

    @OneToOne
    @JoinColumn(name = "order_Id")
    private OrderEntity order;

    @OneToMany(targetEntity = Product.class,cascade = CascadeType.ALL)
    @JoinColumn(name = "product_id")
    private ArrayList<ProductEntity> product;
}
