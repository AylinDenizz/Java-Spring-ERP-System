package com.allianz.erpsystem.service;

import com.allianz.erpsystem.database.entity.InvoiceEntity;
import com.allianz.erpsystem.database.entity.OrderEntity;
import com.allianz.erpsystem.database.entity.ProductEntity;
import com.allianz.erpsystem.database.repository.InvoiceRepository;
import com.allianz.erpsystem.database.repository.ProductRepository;
import com.allianz.erpsystem.model.Invoice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class InvoiceService {
    private static final AtomicLong invoiceNumberGenerator = new AtomicLong(0);


    @Autowired
    InvoiceRepository invoiceRepository;

    @Autowired
    ProductRepository productRepository;

    // random unique Invoice number is created

    public int generateUniqueInvoiceNumber() {
        return (int) invoiceNumberGenerator.incrementAndGet();
    }

    public InvoiceEntity createInvoice( BigDecimal totalPrice, BigDecimal kdvAddedTotalPrice,
                                       OrderEntity orderEntity, List<ProductEntity> productEntities) {

        String newInvoiceNumber = "INV" + generateUniqueInvoiceNumber();

        InvoiceEntity invoice = new InvoiceEntity();
        invoice.setInvoiceNumber(newInvoiceNumber);
        invoice.setOrderEntity(orderEntity);
        invoice.setProductEntity(productEntities);
        invoice.setTotalPrice(totalPrice);
        invoice.setKdvAddedTotalPrice(kdvAddedTotalPrice);


        invoiceRepository.save(invoice);
        return invoice;
    }

    public InvoiceEntity getInvoiceByOrderEntity(OrderEntity orderEntity) {
        Optional<InvoiceEntity> invoiceEntityOptional = invoiceRepository.findInvoiceEntityByOrderEntity(orderEntity);
        if (invoiceEntityOptional.isPresent()) {
            return invoiceEntityOptional.get();
        } else {
            return null;
        }
    }

}
