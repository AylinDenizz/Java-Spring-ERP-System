package com.allianz.erpsystem.controller;

import com.allianz.erpsystem.database.entity.CustomerEntity;
import com.allianz.erpsystem.database.entity.InvoiceEntity;
import com.allianz.erpsystem.database.entity.OrderEntity;
import com.allianz.erpsystem.database.entity.ProductEntity;
import com.allianz.erpsystem.database.repository.OrderRepository;
import com.allianz.erpsystem.database.repository.ProductRepository;
import com.allianz.erpsystem.model.ApprovalStatementEnum;
import com.allianz.erpsystem.model.Order;
import com.allianz.erpsystem.model.Product;
import com.allianz.erpsystem.model.dto.OrderRequestDTO;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    @Autowired
    ProductService productService;


    @PostMapping("make-an-order")
    public ResponseEntity<OrderEntity> createOrder(@RequestBody OrderRequestDTO orderRequest) {
//Calling all the parameteres in the entity was too hard. So created a order request dto and implemented the metot.
        int orderAmount = orderRequest.getOrderAmount();
        String customerNumber = orderRequest.getCustomerNumber();
        List<String> productNumbers = orderRequest.getProductNumbers();

        CustomerEntity customer = customerService.getCustomerByCustomerNumber(customerNumber);

        List<ProductEntity> productEntityList = new ArrayList<>();

        for (String productNumber : productNumbers) {
            ProductEntity product =  productService.getProductByProductNumber(productNumber);
            productEntityList.add(product);
        }

        if (productEntityList != null) {
            //  try {
            OrderEntity order1 = orderService.createOrder(orderAmount,
                    customer, ApprovalStatementEnum.PENDING.getValue(),
                    productEntityList);

            System.out.println(order1);
            customerService.addOrderToCustomer(order1, customer);

           return new ResponseEntity<>(order1, HttpStatus.CREATED);

        }

        return new ResponseEntity<>(null,HttpStatus.NOT_FOUND);
    }





    @PutMapping("approve-order-and-create-invoice/{orderNumber}")
    public ResponseEntity<InvoiceEntity> approveOrderAndCreateInvoice(@PathVariable String orderNumber) {
        // getting order by orderNumber and update it's approval statement.

        OrderEntity orderEntity1 = orderService.getOrderByOrderNumber(orderNumber);

        //ApprovalStatementEnum.PENDİNG => 1
        //ApprovalStatementEnum.APPROVED => 2
        //ApprovalStatementEnum.DECLINED => 3
        //ApprovalStatementEnum.TRANSFERSTATE => 4
        //ApprovalStatementEnum.SALE_COMPLETED => 5

        if (orderEntity1.getApprovalStatement() == 3) {
            System.out.println("Sorry, this order just denied. Please make another order.");
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);

        } else if (orderEntity1.getApprovalStatement() == 4) {
            System.out.println("This order already in Transfer. Here this is product invoice");

            //  return new ResponseEntity<>(null, HttpStatus.NOT_FOUND); // already created invoice will find.
        } else if (orderEntity1.getApprovalStatement() == 5) {
            System.out.println("This order already registered. Here this is product invoice");

            //  return new ResponseEntity<>(null, HttpStatus.NOT_FOUND); // already created invoice will find.
        } else if (orderEntity1.getApprovalStatement() == 1) {

            orderService.approvalStatementChangeOrder(orderEntity1, ApprovalStatementEnum.APPROVED);

            //total price and tax rate added.
            BigDecimal totalPrice = new BigDecimal(0);
            BigDecimal kdvAddedTotalPrice = new BigDecimal(0);

            // created a loop for calculate kdv settings and check for stock amount for all products.
            for (ProductEntity product : orderService.getOrderByOrderNumber(orderNumber
            ).getProductEntities()) {

                /* in this row, it is examined the stock amount of product. If there is enough product,
                 the product stock amount decresed as the order amount.
                */
                if (product.getStockAmount() <= orderEntity1.getOrderAmount()) {
                    System.out.println("Product stock is not enough");
                    orderService.approvalStatementChangeOrder(orderEntity1, ApprovalStatementEnum.DECLINED);
                    return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
                } else {
                    orderService.approvalStatementChangeOrder(orderEntity1, ApprovalStatementEnum.APPROVED);
                    product.setStockAmount(product.getStockAmount() - orderEntity1.getOrderAmount());
                }

                // created a model to calculate kdv settings.
                Order order1 = new Order();
                Double kdvValue = 0.0;

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
                kdvAddedTotalPrice.add(product.getUnitPrice());
                kdvAddedTotalPrice.add(product.getUnitPrice().multiply(new BigDecimal(kdvValue).
                        divide(new BigDecimal(100))));
            }


            // If the order status is "Approved" here, a new invoice will be created.
            InvoiceEntity invoice1 = invoiceService.createInvoice(totalPrice, kdvAddedTotalPrice,
                    orderEntity1, orderEntity1.getProductEntities());
            orderEntity1.setInvoiceEntity(invoice1);
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

