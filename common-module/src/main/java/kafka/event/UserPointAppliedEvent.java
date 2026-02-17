package kafka.event;

import enumtype.PaymentProvider;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@AllArgsConstructor
@Getter
@Builder
public class UserPointAppliedEvent {
    private  String eventId;
    private String orderId;
    private Long dealId;
    private Long userId;
    private Integer quantity;
    private BigDecimal totalAmount;
    private BigDecimal usedPoint;
    private String paymentKey;
    private PaymentProvider provider;
}
