package enumtype;

import exception.NotSupportedOrderStatus;

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
    FAIL_PAYMENT_CANCELED, // 승인된 결제가 취소된 상태
    FAIL_PAYMENT_ABORTED, // 결제 승인 실패
    FAIL_PAYMENT_EXPIRED, // 결제 유효시간 초과
    FAIL_REJECT_CARD_COMPANY, // 구매자의 카드 정보 문제
    ;

    public static OrderStatus from(String status) {
        return switch (status) {
            case "DONE" -> PAYMENT_DONE;
            case "CANCELED", "PAY_PROCESS_CANCELED" -> FAIL_PAYMENT_CANCELED;
            case "ABORTED", "PAY_PROCESS_ABORTED", "-708" -> FAIL_PAYMENT_ABORTED;
            case "EXPIRED", "-723" -> FAIL_PAYMENT_EXPIRED;
            case "REJECT_CARD_COMPANY" -> FAIL_REJECT_CARD_COMPANY;
            default -> throw new NotSupportedOrderStatus("Not supported order status.");
        };
    }
}
