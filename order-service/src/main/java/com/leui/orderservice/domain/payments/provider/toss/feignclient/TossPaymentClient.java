package com.leui.orderservice.domain.payments.provider.toss.feignclient;

import com.leui.orderservice.domain.payments.dto.provider.TossCancelParam;
import com.leui.orderservice.domain.payments.dto.provider.TossApproveResponse;
import com.leui.orderservice.domain.payments.dto.provider.TossSuccessParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "tossPaymentClient", url = "https://api.tosspayments.com")
public interface TossPaymentClient {

    @PostMapping("/v1/payments/confirm")
    ResponseEntity<TossApproveResponse> approve(
            @RequestHeader("Authorization") String authorization,
            @RequestBody TossSuccessParam body
    );

    /*
    curl --request POST \
  --url https://api.tosspayments.com/v1/payments/5EnNZRJGvaBX7zk2yd8ydw26XvwXkLrx9POLqKQjmAw4b0e1/cancel \
  --header 'Authorization: Basic dGVzdF9za196WExrS0V5cE5BcldtbzUwblgzbG1lYXhZRzVSOg==' \
  --header 'Content-Type: application/json' \
  --data '{"cancelReason":"구매자 변심"}'
     */
    @PostMapping("/v1/payments/{paymentKey}/cancel")
    ResponseEntity<Void> cancel(
            @RequestHeader("Authorization") String authorization,
            @PathVariable String paymentKey,
            @RequestBody TossCancelParam body
    );

}
