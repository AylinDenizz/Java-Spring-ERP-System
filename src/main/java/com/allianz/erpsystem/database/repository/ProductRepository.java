package com.allianz.erpsystem.database.repository;

import com.allianz.erpsystem.database.entity.OrderEntity;
import com.allianz.erpsystem.database.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository <ProductEntity, Long> {

    Optional<ProductEntity> findByProductNumber(String key);
    @Modifying
    void deleteByProductNumber(String productNumber);

}


