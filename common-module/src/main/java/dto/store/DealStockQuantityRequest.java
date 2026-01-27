package dto.store;

public record DealStockQuantityRequest(
        String orderId,
        int quantity
) {
}
