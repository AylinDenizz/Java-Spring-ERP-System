package com.allianz.erpsystem.database.entity;

import com.allianz.erpsystem.model.ApprovalStatementEnum;
import com.allianz.erpsystem.model.Customer;
import com.allianz.erpsystem.model.Invoice;
import com.allianz.erpsystem.model.Product;
import com.allianz.erpsystem.util.dbutil.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.*;

@Entity
@Table
@AttributeOverride(
        name = "uuid",
        column = @Column(
                name = "order_uuid"
        )
)
@Data
public class OrderEntity extends BaseEntity {
    @Column
    private int orderAmount;
    @Column
    private String orderNumber;

    @ManyToOne
    @PrimaryKeyJoinColumn
    @JoinColumn(name = "customer_id")
    @JsonIgnore
    private CustomerEntity customerEntity;


    @OneToOne
    @PrimaryKeyJoinColumn
    @JoinColumn(name = "invoice_id")
    private InvoiceEntity invoiceEntity;

    @Column
    @OneToMany(targetEntity = ProductEntity.class, cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "product_id")
    private List<ProductEntity> productEntities;


    @Column
    private int approvalStatement;

}