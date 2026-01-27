package com.leui.orderservice.domain.payments.dto.provider;

import dto.payment.KakaoSuccessParam;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class KakaoConfirmRequest {
    private String orderId;
    private String userId;
    private String pgToken;
    private String cid;
    private String tid;

    public KakaoConfirmRequest(String cid, String tid, KakaoSuccessParam param) {
        this.orderId = param.getOrderId();
        this.userId = param.getUserId();
        this.pgToken = param.getPgToken();
        this.cid = cid;
        this.tid = tid;
    }
}
