package com.allianz.erpsystem.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class OrderRequestDTO {
    private int orderAmount;
    private Long customer_id;
    private List<Long> product_idList;

    // getter ve setter metods
}

