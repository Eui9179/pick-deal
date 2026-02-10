package dto.payment;

import enumtype.OrderStatus;

public record PaymentFailResponse(
        String orderId,
        OrderStatus status
) {
}
