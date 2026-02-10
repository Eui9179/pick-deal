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
    private String cid;
    private String tid;
    private String partnerOrderId;
    private String partnerUserId;
    private String pgToken;

    public KakaoConfirmRequest(String cid, String tid, KakaoSuccessParam param) {
        this.cid = cid;
        this.tid = tid;
        this.partnerOrderId = param.getOrderId();
        this.partnerUserId = param.getUserId();
        this.pgToken = param.getPgToken();
    }
}
