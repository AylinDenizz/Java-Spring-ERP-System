package com.allianz.erpsystem.database.entity;

import com.allianz.erpsystem.util.dbutil.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Table
@AttributeOverride(
        name = "uuid",
        column = @Column(
                name = "product_uuid"
        )
)
@Data
public class ProductEntity extends BaseEntity {

    @Column
    private String productNumber;
    @Column
    private String productType;
    @Column
    private String productName;
    @Column
    private int stockAmount;
    @Column
    private BigDecimal unitPrice;

    @ManyToOne
    private OrderEntity orderEntity;


}