package com.allianz.erpsystem.service;

import com.allianz.erpsystem.database.entity.CustomerEntity;
import com.allianz.erpsystem.database.entity.InvoiceEntity;
import com.allianz.erpsystem.database.entity.OrderEntity;
import com.allianz.erpsystem.database.entity.ProductEntity;
import com.allianz.erpsystem.database.repository.CustomerRepository;
import com.allianz.erpsystem.model.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class CustomerService {

    @Autowired
    CustomerRepository customerRepository;

    // random unique Customer number is created
    private static final AtomicLong customerNumberGenerator = new AtomicLong(0);

    public int generateUniqueCustomerNumber() {
        return (int) customerNumberGenerator.incrementAndGet();
    }

    public CustomerEntity createCustomer(String customerName, String customerAddress) {
        String newCustomerNumber = "CUS" + generateUniqueCustomerNumber();

        CustomerEntity customer = new CustomerEntity();
        customer.setCustomerName(customerName);
        customer.setCustomerAddress(customerAddress);
        customer.setOrderCount(customer.getOrderEntities().size());
        customer.setCustomerNumber(newCustomerNumber);

        customerRepository.save(customer);
        return customer;


    }

    public void addOrderToCustomer(OrderEntity orderEntity, CustomerEntity customerEntity) {
        if (customerEntity.getOrderEntities() != null) {
            customerEntity.getOrderEntities().add(orderEntity);
        } else {
            ArrayList<OrderEntity> orderEntities = new ArrayList<>();
            orderEntities.add(orderEntity);
            customerEntity.setOrderEntities(orderEntities);
        }
    }

    public CustomerEntity getCustomerByCustomerNumber(String customerNumber) {
        Optional<CustomerEntity> customerEntityOptional = customerRepository.findByCustomerNumber(customerNumber);
        if (customerEntityOptional.isPresent()) {
            return customerEntityOptional.get();
        } else {
            return null;
        }
    }


    @Transactional
    public Boolean deleteCustomerByCustomerNumber(String customerNumber) {
        CustomerEntity customerEntity = getCustomerByCustomerNumber(customerNumber);

        if (customerEntity != null) {
            customerRepository.deleteByCustomerNumber(customerNumber);
            return true;
        } else {
            return false;
        }

    }

}
