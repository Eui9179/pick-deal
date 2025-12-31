package com.leui.storeservice.domain.discountpolicy.entity;

import java.math.BigDecimal;

public enum DiscountType {

    PERCENT {
        @Override
        public BigDecimal calculate(BigDecimal price, BigDecimal discount) {
            return null;
        }
    },

    AMOUNT {
        @Override
        public BigDecimal calculate(BigDecimal price, BigDecimal discount) {
            return null;
        }
    }
    ;

    public abstract BigDecimal calculate(BigDecimal price, BigDecimal discount);

}
