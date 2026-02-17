package kafka.event;

import enumtype.PaymentProvider;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class DealStockCommitFailEvent {
    private String eventId;
    private String orderId;
    private Long dealId;
    private Long userId;
    private Integer quantity;
    private BigDecimal totalAmount;
    private BigDecimal usedPoint;
    private String paymentKey;
    private PaymentProvider provider;
}
