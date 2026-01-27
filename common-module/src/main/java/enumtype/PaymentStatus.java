package enumtype;

public enum PaymentStatus {
    READY,      // 결제 준비 (ready 호출 완료)
    DONE,       // 결제 완료 (confirm 성공)
    FAILED,     // 결제 실패
    CANCELLED   // 결제 취소
}
