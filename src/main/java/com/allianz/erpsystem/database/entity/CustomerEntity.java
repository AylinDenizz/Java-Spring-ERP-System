package com.allianz.erpsystem.database.entity;

import com.allianz.erpsystem.model.Order;
import com.allianz.erpsystem.model.Product;
import com.allianz.erpsystem.util.dbutil.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table
@AttributeOverride(
        name = "uuid",
        column = @Column(
                name = "customer_uuid"
        )
)
@Data
public class CustomerEntity extends BaseEntity {

    @Column
    private String customerAddress;
    @Column
    private String customerName;
    @Column
    private int orderCount;

    @Column
    @OneToMany(mappedBy = "customerEntity" ,targetEntity =OrderEntity.class, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderEntity> orderEntities = null;




}