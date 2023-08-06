package com.allianz.erpsystem.controller;

import com.allianz.erpsystem.database.entity.OrderEntity;
import com.allianz.erpsystem.database.entity.ProductEntity;
import com.allianz.erpsystem.database.repository.CustomerRepository;
import com.allianz.erpsystem.database.repository.ProductRepository;
import com.allianz.erpsystem.model.Customer;
import com.allianz.erpsystem.model.Product;
import com.allianz.erpsystem.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.sound.sampled.AudioSystem;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("product")
public class ProductController {


    @Autowired
    private ProductRepository productRepository;

    @Autowired
    ProductService productService;

    @PostMapping("create-product")
    public ResponseEntity<ProductEntity> createProduct(@RequestBody ProductEntity productEntity) {
        ProductEntity product1 = productService.createProduct(productEntity.getProductName(),
                productEntity.getStockAmount(), productEntity.getUnitPrice(), productEntity.getProductType());

        return new ResponseEntity<>(product1, HttpStatus.CREATED);

    }

    @GetMapping("product-get-by-product-number/{productNumber}")
    public ResponseEntity<ProductEntity> getProductByProductNumber(@PathVariable String productNumber) {
        ProductEntity productEntity1 = productService.getProductByProductNumber(productNumber);
        return new ResponseEntity<>(productEntity1, HttpStatus.OK);
    }

    @PutMapping("update-product-by-product-name/{productNumber}")
    public ResponseEntity<ProductEntity> updateProductByProductName(@PathVariable String productNumber,
                                                            @RequestBody ProductEntity newProductEntity) {
        ProductEntity productEntity1 = productService.getProductByProductNumber(productNumber);
        productEntity1.setProductNumber(newProductEntity.getProductNumber());
        productEntity1.setProductName(newProductEntity.getProductName());
        productEntity1.setProductType(newProductEntity.getProductType());
        productEntity1.setStockAmount(newProductEntity.getStockAmount());
        productEntity1.setUnitPrice(newProductEntity.getUnitPrice());
        productService.updateProduct(productEntity1);
        return new ResponseEntity<>(productEntity1, HttpStatus.OK);

    }

    @PutMapping("update-product-price/{productNumber}")
    public ResponseEntity<ProductEntity> updateProductPrice(@PathVariable String productNumber,
                                                            @RequestBody BigDecimal productPrice) {

        ProductEntity productEntity1 = productService.getProductByProductNumber(productNumber);
        productEntity1.setUnitPrice(productPrice);
        productService.updateProduct(productEntity1);
        return new ResponseEntity<>(productEntity1, HttpStatus.OK);

    }

    @Transactional
    @DeleteMapping("product-delete-by-product-number/{productNumber}")
    public ResponseEntity<Boolean> deleteProductByProductNumber(@PathVariable String productNumber) {

        Boolean isDeleted = productService.deleteProductByProductNumber(productNumber);
        if (isDeleted) {
            return new ResponseEntity<>(Boolean.TRUE, HttpStatus.OK);

        } else {
            return new ResponseEntity<>(Boolean.FALSE, HttpStatus.NOT_FOUND);

        }
    }









}
