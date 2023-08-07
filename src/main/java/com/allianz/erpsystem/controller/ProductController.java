package com.allianz.erpsystem.controller;

import com.allianz.erpsystem.database.entity.ProductEntity;
import com.allianz.erpsystem.database.repository.ProductRepository;
import com.allianz.erpsystem.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

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

    @GetMapping("product-get-by-Id/{id}")
    public ResponseEntity<ProductEntity> getProductById(@PathVariable Long id) {
        ProductEntity productEntity1 = productService.getProductById(id);
        return new ResponseEntity<>(productEntity1, HttpStatus.OK);
    }

    @PutMapping("update-product-by-Id/{id}")
    public ResponseEntity<ProductEntity> updateProductById(@PathVariable Long id,
                                                            @RequestBody ProductEntity newProductEntity) {
        ProductEntity productEntity1 = productService.getProductById(id);
        productEntity1.setProductName(newProductEntity.getProductName());
        productEntity1.setProductType(newProductEntity.getProductType());
        productEntity1.setStockAmount(newProductEntity.getStockAmount());
        productEntity1.setUnitPrice(newProductEntity.getUnitPrice());
        productService.updateProduct(productEntity1);
        return new ResponseEntity<>(productEntity1, HttpStatus.OK);

    }

    @PutMapping("update-product-price-by-id/{id}")
    public ResponseEntity<ProductEntity> updateProductPriceById(@PathVariable Long id,
                                                            @RequestBody BigDecimal productPrice) {

        ProductEntity productEntity1 = productService.getProductById(id);
        if (productEntity1 != null) {
        productEntity1.setUnitPrice(productPrice);
        productService.updateProduct(productEntity1);
        return new ResponseEntity<>(productEntity1, HttpStatus.OK);}
        else{
            System.out.println("there is no such thing has a product number like this.");
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);}

    }



    @Transactional
    @DeleteMapping("product-delete-by-id/{id}")
    public ResponseEntity<Boolean> deleteProductById(@PathVariable Long id) {

        Boolean isDeleted = productService.deleteProductById(id);
        if (isDeleted) {
            return new ResponseEntity<>(Boolean.TRUE, HttpStatus.OK);

        } else {
            return new ResponseEntity<>(Boolean.FALSE, HttpStatus.NOT_FOUND);

        }
    }









}
