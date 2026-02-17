package kafka.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@AllArgsConstructor
@Builder
@Getter
public class PaymentCancelEvent {
    private String eventId;
    private String orderId;
    private Long dealId;
    private Long userId;
    private Integer quantity;
    private BigDecimal totalAmount;
    private String paymentKey;
    private String failDescription;
}
