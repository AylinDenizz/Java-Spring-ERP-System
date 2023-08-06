package com.allianz.erpsystem.database.entity;

import com.allianz.erpsystem.model.ApprovalStatementEnum;
import com.allianz.erpsystem.model.Customer;
import com.allianz.erpsystem.model.Invoice;
import com.allianz.erpsystem.model.Product;
import com.allianz.erpsystem.util.dbutil.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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

    @OneToOne
    @PrimaryKeyJoinColumn
    @JoinColumn(name = "customer_id")
    private CustomerEntity customerEntity;


    @OneToOne
    @PrimaryKeyJoinColumn
    @JoinColumn(name = "invoice_id")
    private InvoiceEntity invoiceEntity;

    @Column
    @OneToMany(targetEntity = ProductEntity.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "product_id")
    private ArrayList<ProductEntity> productEntities;


    @Column
    @Enumerated(EnumType.STRING)
    private ApprovalStatementEnum approvalStatement;

}
