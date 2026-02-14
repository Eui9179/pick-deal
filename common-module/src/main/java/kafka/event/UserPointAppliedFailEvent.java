package kafka.event;

import enumtype.PaymentProvider;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@AllArgsConstructor
@Builder
@Getter
public class UserPointAppliedFailEvent {
    private String orderId;
    private Long dealId;
    private Long userId;
    private Integer quantity;
    private BigDecimal totalAmount;
    private BigDecimal usedPoint;
    private String paymentKey;
    private PaymentProvider provider;
}
