package dto.payment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TossSuccessParam extends PaymentSuccessParam {
    private String paymentType;
    private String orderId;
    private String paymentKey;
    private BigDecimal amount;
}
