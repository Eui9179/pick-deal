package dto.payment;

import enumtype.TossPaymentType;

import java.math.BigDecimal;

public record TossSuccessParam(
        TossPaymentType paymentType,
        String orderId,
        String paymentKey,
        BigDecimal amout
) {
}
