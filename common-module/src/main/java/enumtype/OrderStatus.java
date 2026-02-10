package enumtype;

public enum OrderStatus {

    // 주문 관련
    ORDER_START, // 주문 시작
    ORDER_CANCELED, // 주문 취소
    ORDER_READY_FOR_PICKUP, // 픽업 대기
    ORDER_COMPLETE, // 주문 완료

    // 서비스 실패
    FAIL_DEAL_NOTFOUND, // 상품 NotFound
    FAIL_OUT_OF_STOCK, // 품절

    // PG 관련
    PAYMENT_DONE, // 결제 완료
    PAYMENT_FAILED, // 결제 실패
    ;
}
