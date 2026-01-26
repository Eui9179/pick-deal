package dto.payment;

public record PaymentFailParam(
        String orderId,
        String code
) {
}
