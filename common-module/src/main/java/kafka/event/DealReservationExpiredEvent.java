package kafka.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class DealReservationExpiredEvent {
    private Long dealId;
    private String orderId;
    private Long userId;
    private int quantity;
    private long expiredAt;
}
