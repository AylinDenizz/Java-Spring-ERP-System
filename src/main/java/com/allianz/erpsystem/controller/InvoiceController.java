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
import org.springframework.web.bind.annotation.*;

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


    @GetMapping("get-invoice-by-id/{id}")
    public  ResponseEntity<InvoiceEntity> getInvoice(@PathVariable Long id) {
        InvoiceEntity invoice1 = invoiceService.getInvoiceById(id);
        return new ResponseEntity<>(invoice1, HttpStatus.OK);


    }


}
