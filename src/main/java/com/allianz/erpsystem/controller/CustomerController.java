package com.allianz.erpsystem.controller;

import com.allianz.erpsystem.database.entity.CustomerEntity;
import com.allianz.erpsystem.database.entity.InvoiceEntity;
import com.allianz.erpsystem.database.entity.ProductEntity;
import com.allianz.erpsystem.database.repository.CustomerRepository;
import com.allianz.erpsystem.database.repository.InvoiceRepository;
import com.allianz.erpsystem.database.repository.OrderRepository;
import com.allianz.erpsystem.database.repository.ProductRepository;
import com.allianz.erpsystem.model.Customer;
import com.allianz.erpsystem.model.Invoice;
import com.allianz.erpsystem.service.CustomerService;
import com.allianz.erpsystem.service.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("customer")
public class CustomerController {


    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    CustomerService customerService;


    @PostMapping("create-customer")
    public ResponseEntity<CustomerEntity> createCustomer(@RequestBody CustomerEntity customer) {
        CustomerEntity customer1 = customerService.createCustomer(customer.getCustomerName(),
                customer.getCustomerAddress()
        );

        return new ResponseEntity<>(customer1, HttpStatus.CREATED);
    }

    @Transactional
    @DeleteMapping("customer-delete-by-customer-number/{customerNumber}")
    public ResponseEntity<Boolean> deleteCustomerByCustomerNumber(@PathVariable String customerNumber) {

        Boolean isDeleted = customerService.deleteCustomerByCustomerNumber(customerNumber);
        if (isDeleted) {
            return new ResponseEntity<>(Boolean.TRUE, HttpStatus.OK);

        } else {
            return new ResponseEntity<>(Boolean.FALSE, HttpStatus.NOT_FOUND);

        }
    }


    @GetMapping("customer-get-by-customer-number/{customerNumber}")
    public ResponseEntity<CustomerEntity> getCustomer(@PathVariable String customerNumber) {
        CustomerEntity customerEntity = customerService.getCustomerByCustomerNumber(customerNumber);
        return new ResponseEntity<>(customerEntity, HttpStatus.OK);
    }



}


