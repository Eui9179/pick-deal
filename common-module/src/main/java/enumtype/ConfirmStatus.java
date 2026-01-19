package enumtype;

public enum ConfirmStatus {
    DONE, // 결제 완료
    CANCELED, // 승인된 결제가 취소된 상태
    ABORTED, //결제 승인 실패
    EXPIRED, // 결제 유효시간 초과
    ;

    public static ConfirmStatus from(String status) {
        return ConfirmStatus.valueOf(status);
    }
}
