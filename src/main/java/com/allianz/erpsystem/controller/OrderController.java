package com.allianz.erpsystem.controller;

import com.allianz.erpsystem.database.entity.InvoiceEntity;
import com.allianz.erpsystem.database.entity.OrderEntity;
import com.allianz.erpsystem.database.entity.ProductEntity;
import com.allianz.erpsystem.database.repository.OrderRepository;
import com.allianz.erpsystem.database.repository.ProductRepository;
import com.allianz.erpsystem.model.ApprovalStatementEnum;
import com.allianz.erpsystem.model.Order;
import com.allianz.erpsystem.model.Product;
import com.allianz.erpsystem.service.CustomerService;
import com.allianz.erpsystem.service.InvoiceService;
import com.allianz.erpsystem.service.OrderService;
import com.allianz.erpsystem.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;


@RestController
@RequestMapping("order")
public class OrderController {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    OrderService orderService;

    @Autowired
    CustomerService customerService;

    @Autowired
    InvoiceService invoiceService;


    @PostMapping("make-an-order")
    public ResponseEntity<OrderEntity> createOrder(@RequestBody OrderEntity orderEntity) {
        OrderEntity order1 = orderService.createOrder(orderEntity.getOrderAmount(),
                orderEntity.getCustomerEntity(), ApprovalStatementEnum.PENDING, orderEntity.getInvoiceEntity(),
                orderEntity.getProductEntities());
        customerService.addOrderToCustomer(orderEntity, orderEntity.getCustomerEntity());

        return new ResponseEntity<>(order1, HttpStatus.CREATED);

    }


    @PutMapping("approve-order/{orderNumber}")
    public ResponseEntity<InvoiceEntity> approveOrder(@PathVariable String orderNumber) {
        // getting order by orderNumber and update it's approval statement.

        OrderEntity orderEntity1 = orderService.getOrderByOrderNumber(orderNumber);

        if (orderEntity1.getApprovalStatement().equals(ApprovalStatementEnum.DECLINED)) {
            System.out.println("Sorry, this order just denied. Please make another order.");
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);

        } else if (orderEntity1.getApprovalStatement().equals(ApprovalStatementEnum.TRANSFERSTATE)) {
            System.out.println("This order already in Transfer. Here this is product invoice");

            //  return new ResponseEntity<>(null, HttpStatus.NOT_FOUND); // already created invoice will find.
        } else if (orderEntity1.getApprovalStatement().equals(ApprovalStatementEnum.SALE_COMPLETED)) {
            System.out.println("This order already registered. Here this is product invoice");

            //  return new ResponseEntity<>(null, HttpStatus.NOT_FOUND); // already created invoice will find.
        } else if (orderEntity1.getApprovalStatement().equals(ApprovalStatementEnum.PENDING)) {

            orderService.approvalStatementChangeOrder(orderEntity1, ApprovalStatementEnum.APPROVED);

            //total price and tax rate added.
            BigDecimal totalPrice = new BigDecimal(0);
            BigDecimal kdvAddedTotalPrice = new BigDecimal(0);
            Order order1 = new Order();
            Double kdvValue = 0.0;

            // calculate all the kdv added prices for all product and add that to kdvAddedTotalPrice

            for (ProductEntity product : orderService.getOrderByOrderNumber(orderNumber
            ).getProductEntities()) {

                //check the product stock amount if exist enough or not

                totalPrice = totalPrice.add(product.getUnitPrice());
                if (product.getProductType() == "food") {
                    kdvValue = order1.getKdvSetting("kdv1");
                } else if (product.getProductType() == "electronics") {
                    kdvValue = order1.getKdvSetting("kdv18");
                } else if (product.getProductType() == "services") {
                    kdvValue = order1.getKdvSetting("kdv8");
                } else {
                    System.out.println("please choose a product type as food, electronics or services");
                    return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
                }
                /* in this row, it is examined the stock amount of product. If there is enough product,
                 the product stock amount decresed as the order amount.
                */
                if (product.getStockAmount() <= 0) {
                    System.out.println("Product stock is not enough");
                    orderService.approvalStatementChangeOrder(orderEntity1, ApprovalStatementEnum.DECLINED);
                    return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
                } else {
                    orderService.approvalStatementChangeOrder(orderEntity1, ApprovalStatementEnum.APPROVED);
                    product.setStockAmount(product.getStockAmount() - order1.getOrderAmount());
                }
                kdvAddedTotalPrice.add(product.getUnitPrice().multiply(new BigDecimal(kdvValue).
                        divide(new BigDecimal(100)).add(product.getUnitPrice())));
            }


            // If the order status is "Approved" here, a new invoice will be created.
            InvoiceEntity invoice1 = invoiceService.createInvoice(totalPrice, kdvAddedTotalPrice,
                    orderEntity1, orderEntity1.getProductEntities());
            System.out.println("Invoice has been created");
            return new ResponseEntity<>(invoice1, HttpStatus.CREATED);
        }
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }
// update metotu oluşturulacak ve kargoda veya satış tamamlandı aşamaları güncellenebilecek. Ama diğer şeyler değiştirilemeyecek.

    @PutMapping("make-sale-completed/{orderNumber}")
    public ResponseEntity<OrderEntity> makeSaleCompleted(@PathVariable String orderNumber) {
        // getting order by orderNumber and update it's approval statement as SALE_COMPLETED

        OrderEntity orderEntity1 = orderService.getOrderByOrderNumber(orderNumber);
        orderService.approvalStatementChangeOrder(orderEntity1, ApprovalStatementEnum.SALE_COMPLETED);

        return new ResponseEntity<>(orderEntity1, HttpStatus.OK);

    }

    @PutMapping("transfer-order/{orderNumber}")
    public ResponseEntity<OrderEntity> changeApprovalAsTransferstate(@PathVariable String orderNumber) {
        // getting order by orderNumber and update it's approval statement as TRANSFERSTATE

        OrderEntity orderEntity1 = orderService.getOrderByOrderNumber(orderNumber);
        orderService.approvalStatementChangeOrder(orderEntity1, ApprovalStatementEnum.TRANSFERSTATE);
        return new ResponseEntity<>(orderEntity1, HttpStatus.OK);


    }

}

