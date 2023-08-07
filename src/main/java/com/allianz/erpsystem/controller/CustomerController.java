package com.allianz.erpsystem.controller;

import com.allianz.erpsystem.database.entity.CustomerEntity;
import com.allianz.erpsystem.database.repository.CustomerRepository;
import com.allianz.erpsystem.database.repository.OrderRepository;
import com.allianz.erpsystem.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

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
    @DeleteMapping("customer-delete-by-id/{id}")
    public ResponseEntity<Boolean> deleteCustomerById(@PathVariable Long id) {

        Boolean isDeleted = customerService.deleteCustomerByCustomerNumber(id);
        if (isDeleted) {
            return new ResponseEntity<>(Boolean.TRUE, HttpStatus.OK);

        } else {
            return new ResponseEntity<>(Boolean.FALSE, HttpStatus.NOT_FOUND);

        }
    }


    @GetMapping("customer-get-by-id/{id}")
    public ResponseEntity<CustomerEntity> getCustomer(@PathVariable  Long id) {
        CustomerEntity customerEntity = customerService.getCustomerById(id);
        return new ResponseEntity<>(customerEntity, HttpStatus.OK);
    }



}


