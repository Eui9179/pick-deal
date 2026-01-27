package dto.payment;

import enumtype.PaymentProvider;

public record PaymentFailRequest(
        String orderId,
        PaymentProvider provider,
        String failCode
) {
}
