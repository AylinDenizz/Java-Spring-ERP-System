package com.allianz.erpsystem.database.repository;

import com.allianz.erpsystem.database.entity.CustomerEntity;
import com.allianz.erpsystem.database.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<CustomerEntity, Long> {

    Optional<CustomerEntity> findCustomerEntitiesByCustomerNumber(String customerNumber);
    Optional<CustomerEntity> findByCustomerNumber(String customerNumber);
    Optional<CustomerEntity> findCustomerEntityByCustomerNumber(String customerNumber);

    @Modifying
    void deleteCustomerEntityByCustomerNumber(String customerNumber);

}
