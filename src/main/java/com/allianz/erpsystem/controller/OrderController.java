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

            System.out.println(order1);
            customerService.addOrderToCustomer(order1, customer);

            return new ResponseEntity<>(order1, HttpStatus.CREATED);

        }

        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }


    @PutMapping("approve-order-and-create-invoice/by-id/{id}")
    public ResponseEntity<InvoiceEntity> approveOrderAndCreateInvoice(@PathVariable Long id) {
        // getting order by orderNumber and update it's approval statement.

        OrderEntity orderEntity1 = orderService.getOrderById(id);

        InvoiceEntity invoice1 = new InvoiceEntity();

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

            //total price and tax rate added.
            BigDecimal totalPrice = new BigDecimal(0);
            BigDecimal kdvAddedTotalPrice = new BigDecimal(0);
            List<ProductEntity> productEntities = orderEntity1.getProductEntities();

            // created a loop for calculate kdv settings and check for stock amount for all products.
            for (ProductEntity product : productEntities) {

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

                orderService.approvalStatementChangeOrder(orderEntity1, ApprovalStatementEnum.APPROVED);


                // If the order status is "Approved" here, a new invoice will be created.
                invoice1 = invoiceService.createInvoice(totalPrice, kdvAddedTotalPrice,
                        orderEntity1, orderEntity1.getProductEntities());
                orderEntity1.setInvoiceEntity(invoice1);
                System.out.println("Invoice has been created");
                return new ResponseEntity<>(invoice1, HttpStatus.CREATED);
            }
            return new ResponseEntity<>(invoice1, HttpStatus.CREATED);
        }
        return new ResponseEntity<>(invoice1, HttpStatus.CREATED);

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

