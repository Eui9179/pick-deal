package dto.payment;

import enumtype.OrderStatus;

public record PaymentFailResponse(
        OrderStatus status
) {
}
