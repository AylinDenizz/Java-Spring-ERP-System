package com.allianz.erpsystem.service;

import com.allianz.erpsystem.database.entity.CustomerEntity;
import com.allianz.erpsystem.database.entity.InvoiceEntity;
import com.allianz.erpsystem.database.entity.OrderEntity;
import com.allianz.erpsystem.database.entity.ProductEntity;
import com.allianz.erpsystem.database.repository.OrderRepository;
import com.allianz.erpsystem.model.ApprovalStatementEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class OrderService {

    // random unique Order number is created
    private static final AtomicLong orderNumberGenerator = new AtomicLong(0);

    public int generateUniqueOrderNumber() {
        return (int) orderNumberGenerator.incrementAndGet();
    }


    @Autowired
    OrderRepository orderRepository;


    public OrderEntity createOrder(int orderAmount, CustomerEntity customerEntity,
                                   ApprovalStatementEnum approvalStatementEnum, InvoiceEntity invoiceEntity,
                                   ArrayList<ProductEntity> productEntities) {
        String newOrderNumber = "ORD" + generateUniqueOrderNumber();

        OrderEntity order = new OrderEntity();
        order.setOrderAmount(orderAmount);
        order.setProductEntities(productEntities);
        order.setInvoiceEntity(invoiceEntity);
        order.setCustomerEntity(customerEntity);
        order.setApprovalStatement(approvalStatementEnum);
        order.setOrderNumber(newOrderNumber);

        orderRepository.save(order);

        return order;
    }

    public OrderEntity getOrderByOrderNumber(String orderNumber) {
        Optional<OrderEntity> orderEntityOptional = orderRepository.findByOrderNumber(orderNumber);
        if (orderEntityOptional.isPresent()) {
            return orderEntityOptional.get();
        } else {
            return null;
        }
    }

    public OrderEntity approvalStatementChangeOrder(OrderEntity orderEntity, ApprovalStatementEnum approvalStatementEnum) {
        OrderEntity orderEntity1 = getOrderByOrderNumber(orderEntity.getOrderNumber());
        if (orderEntity1 != null) {

            orderEntity1.setApprovalStatement(approvalStatementEnum);

            orderRepository.save(orderEntity1);

            return orderEntity1;
        } else {
            return null;


        }
    }


}
