package kafka.event;

import enumtype.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentDoneEvent {
    private String orderId;
    private Long dealId;
    private Long userId;
    private Integer quantity;
    private BigDecimal totalAmount;
    private String paymentKey;
}