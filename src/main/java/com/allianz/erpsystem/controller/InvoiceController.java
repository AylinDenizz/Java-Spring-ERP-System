package com.allianz.erpsystem.controller;

import com.allianz.erpsystem.database.entity.InvoiceEntity;
import com.allianz.erpsystem.database.entity.ProductEntity;
import com.allianz.erpsystem.database.repository.InvoiceRepository;
import com.allianz.erpsystem.database.repository.ProductRepository;
import com.allianz.erpsystem.model.Invoice;
import com.allianz.erpsystem.model.Product;
import com.allianz.erpsystem.service.InvoiceService;
import com.allianz.erpsystem.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("invoice")
public class InvoiceController {


    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    InvoiceService invoiceService;


    public ResponseEntity<InvoiceEntity> createProduct(@RequestBody InvoiceEntity invoiceEntity) {
        InvoiceEntity invoice1 = invoiceService.createInvoice(
                invoiceEntity.getTotalPrice(), invoiceEntity.getKdvAddedTotalPrice(),invoiceEntity.getOrderEntity(),
                invoiceEntity.getProductEntity());

        return new ResponseEntity<>(invoice1, HttpStatus.CREATED);

    }


}
