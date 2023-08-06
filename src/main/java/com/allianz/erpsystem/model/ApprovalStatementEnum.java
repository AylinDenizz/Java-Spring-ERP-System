package com.allianz.erpsystem.model;

import lombok.Data;
import lombok.Getter;

@Getter
public enum ApprovalStatementEnum {
    PENDING(1),
    APPROVED(2),
    DECLINED(3),
    TRANSFERSTATE(4),
    SALE_COMPLETED(5);

    private final int value;

    ApprovalStatementEnum(int value) {
        this.value = value;
    }
}
