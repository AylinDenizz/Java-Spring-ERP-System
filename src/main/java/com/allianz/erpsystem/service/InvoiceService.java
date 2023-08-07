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


    public InvoiceEntity createInvoice(BigDecimal totalPrice, BigDecimal kdvAddedTotalPrice,
                                       OrderEntity orderEntity ) {


        InvoiceEntity invoice = new InvoiceEntity();
        invoice.setOrderEntity(orderEntity);
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

    public InvoiceEntity getInvoiceById(Long id) {
        Optional<InvoiceEntity> invoiceEntityOptional = invoiceRepository.findInvoiceEntityById(id);
        if (invoiceEntityOptional.isPresent()) {
            return invoiceEntityOptional.get();
        } else {
            return null;
        }
    }

}
