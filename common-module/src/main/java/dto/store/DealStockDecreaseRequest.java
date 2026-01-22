package dto.store;

public record DealStockDecreaseRequest(
        String orderId,
        int quantity
) {
}
