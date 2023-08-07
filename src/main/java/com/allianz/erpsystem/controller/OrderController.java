package com.allianz.erpsystem.controller;

import com.allianz.erpsystem.database.entity.CustomerEntity;
import com.allianz.erpsystem.database.entity.InvoiceEntity;
import com.allianz.erpsystem.database.entity.OrderEntity;
import com.allianz.erpsystem.database.entity.ProductEntity;
import com.allianz.erpsystem.database.repository.OrderRepository;
import com.allianz.erpsystem.model.ApprovalStatementEnum;
import com.allianz.erpsystem.model.Order;
import com.allianz.erpsystem.model.dto.OrderRequestDTO;
import com.allianz.erpsystem.service.CustomerService;
import com.allianz.erpsystem.service.InvoiceService;
import com.allianz.erpsystem.service.OrderService;
import com.allianz.erpsystem.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


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

    @GetMapping("get-order-by-id/{id}")
    public ResponseEntity<OrderEntity> getOrder(@PathVariable Long id) {
        OrderEntity order1 = orderService.getOrderById(id);
        return new ResponseEntity<>(order1, HttpStatus.OK);

    }


    @PostMapping("make-an-order")
    public ResponseEntity<OrderEntity> createOrder(@RequestBody OrderRequestDTO orderRequest) {
//Calling all the parameteres in the entity was too hard. So created a order request dto and implemented the metot.
        int orderAmount = orderRequest.getOrderAmount();
        Long customer_id = orderRequest.getCustomer_id();
        List<Long> productIds = orderRequest.getProduct_idList();


        CustomerEntity customer = customerService.getCustomerById(customer_id);

        List<ProductEntity> productEntityList = new ArrayList<>();

        for (Long productID : productIds) {
            ProductEntity product = productService.getProductById(productID);
            productEntityList.add(product);
        }

        if (productEntityList != null) {
            //  try {
            OrderEntity order1 = orderService.createOrder(orderAmount,
                    customer, 1,
                    productEntityList);
            customerService.addOrderToCustomer(order1, customer);

            return new ResponseEntity<>(order1, HttpStatus.CREATED);

        } else {
            System.out.println("there is no product entity");
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }

    }


    @PutMapping("approve-order-and-create-invoice/by-id/{id}")
    public ResponseEntity<InvoiceEntity> approveOrderAndCreateInvoice(@PathVariable Long id) {
        OrderEntity orderEntity = orderService.getOrderById(id);

        if (orderEntity.getApprovalStatement() == 3) {
            System.out.println("Sorry, this order has been declined. Please place another order.");
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } else if (orderEntity.getApprovalStatement() == 4 || orderEntity.getApprovalStatement() == 5) {
            System.out.println("This order is already in transfer or registered. Here is the product invoice.");
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } else if (orderEntity.getApprovalStatement() == 1) {
            BigDecimal totalPrice = BigDecimal.ZERO;
            BigDecimal kdvAddedTotalPrice = BigDecimal.ZERO;

            List<ProductEntity> productEntities = orderEntity.getProductEntities();
            Order orderModel = new Order();

            for (ProductEntity product : productEntities) {
                if (product.getStockAmount() < orderEntity.getOrderAmount()) {
                    System.out.println("Product stock is not enough");
                    orderService.approvalStatementChangeOrder(orderEntity, ApprovalStatementEnum.DECLINED);
                    return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
                } else {

                    BigDecimal productUnitPrice = product.getUnitPrice();
                    totalPrice = totalPrice.add(productUnitPrice);

                    double kdvValue = 0.0;
                    if ("food".equals(product.getProductType())) {
                        kdvValue = orderModel.getKdvSetting("kdv1");
                    } else if ("electronics".equals(product.getProductType())) {
                        kdvValue = orderModel.getKdvSetting("kdv18");
                    } else if ("services".equals(product.getProductType())) {
                        kdvValue = orderModel.getKdvSetting("kdv8");
                    }

                    BigDecimal kdvAmount = productUnitPrice.multiply(BigDecimal.valueOf(kdvValue / 100));
                    kdvAddedTotalPrice = kdvAddedTotalPrice.add(productUnitPrice).add(kdvAmount);

                    product.setStockAmount(product.getStockAmount() - orderEntity.getOrderAmount());
                }

                orderService.approvalStatementChangeOrder(orderEntity, ApprovalStatementEnum.APPROVED);
                InvoiceEntity invoice = invoiceService.createInvoice(totalPrice, kdvAddedTotalPrice,
                        orderEntity);
                orderEntity.setInvoiceEntity(invoice);

                System.out.println("Invoice has been created");
                return new ResponseEntity<>(invoice, HttpStatus.CREATED);
            }

        }
        return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }


// update metotu oluşturulacak ve kargoda veya satış tamamlandı aşamaları güncellenebilecek. Ama diğer şeyler değiştirilemeyecek.

    @PutMapping("make-sale-completed-by-id/{id}")
    public ResponseEntity<OrderEntity> makeSaleCompleted(@PathVariable Long id) {
        // getting order by id and update it's approval statement as SALE_COMPLETED

        OrderEntity orderEntity1 = orderService.getOrderById(id);
        orderService.approvalStatementChangeOrder(orderEntity1, ApprovalStatementEnum.SALE_COMPLETED);

        return new ResponseEntity<>(orderEntity1, HttpStatus.OK);

    }

    @PutMapping("transfer-order-by-id/{id}")
    public ResponseEntity<OrderEntity> changeApprovalAsTransferstate(@PathVariable Long id) {
        // getting order by id and update it's approval statement as TRANSFERSTATE

        OrderEntity orderEntity1 = orderService.getOrderById(id);
        orderService.approvalStatementChangeOrder(orderEntity1, ApprovalStatementEnum.TRANSFERSTATE);
        return new ResponseEntity<>(orderEntity1, HttpStatus.OK);


    }

}

