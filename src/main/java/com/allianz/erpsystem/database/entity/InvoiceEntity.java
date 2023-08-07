package com.allianz.erpsystem.database.entity;

import com.allianz.erpsystem.model.Order;
import com.allianz.erpsystem.model.Product;
import com.allianz.erpsystem.util.dbutil.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table
@AttributeOverride(
        name = "uuid",
        column = @Column(
                name = "invoice_uuid"
        )
)
@Data
public class InvoiceEntity extends BaseEntity {

    @Column
    private String invoiceNumber;
    @Column
    private BigDecimal TotalPrice;
    @Column
    private BigDecimal kdvAddedTotalPrice;

    @OneToOne
    @PrimaryKeyJoinColumn
    private OrderEntity orderEntity;


    @Column
    @OneToMany(targetEntity = ProductEntity.class,cascade = CascadeType.ALL)
    @JsonIgnore
    @JoinColumn(name = "product_id")
    private List<ProductEntity> productEntity;
}