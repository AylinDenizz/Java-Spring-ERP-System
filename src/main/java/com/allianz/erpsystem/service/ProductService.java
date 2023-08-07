package com.allianz.erpsystem.service;

import com.allianz.erpsystem.database.entity.ProductEntity;
import com.allianz.erpsystem.database.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

@Service

public class ProductService {
    @Autowired
    ProductRepository productRepository;

    // random unique Product number is created
    private static final AtomicLong productNumberGenerator = new AtomicLong(0);

    public ProductEntity createProduct( String productName, int stockAmount, BigDecimal price,
                                       String productType) {

        ProductEntity product =new ProductEntity();
        product.setProductName(productName);
        product.setStockAmount(stockAmount);
        product.setUnitPrice(price);
        product.setProductType(productType);

        productRepository.save(product);
        return product;
    }

    public void updateProduct( ProductEntity productEntity) {
        productRepository.save(productEntity);


    }

    public ProductEntity getProductByProductNumber(Long id) {
        Optional<ProductEntity> productEntityOptional = productRepository.findProductEntityById(id);
        if (productEntityOptional.isPresent()) {
            return productEntityOptional.get();
        } else {
            return null;
        }
    }


    public ProductEntity getProductById(Long id) {
        Optional<ProductEntity> productEntityOptional = productRepository.findProductEntityById(id);
        if (productEntityOptional.isPresent()) {
            return productEntityOptional.get();
        } else {
            return null;
        }
    }


    @Transactional
    public Boolean deleteProductById(Long id) {
        ProductEntity productEntity = getProductById(id);

        if (productEntity != null) {
            productRepository.deleteProductEntitiesById(id);
            return true;
        } else {
            return false;
        }

    }


}
