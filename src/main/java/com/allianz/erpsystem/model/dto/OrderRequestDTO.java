package com.allianz.erpsystem.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class OrderRequestDTO {
    private int orderAmount;
    private String customerNumber;
    private List<String> productNumbers;

    // getter ve setter metods
}

