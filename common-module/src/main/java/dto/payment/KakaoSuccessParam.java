package dto.payment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KakaoSuccessParam extends PaymentSuccessParam {
    private String orderId;
    private String userId;
    private String pgToken;
}