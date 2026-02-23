package enumtype;

public enum PaymentProvider {
    TOSS,
    KAKAO
    ;

    public static PaymentProvider from(String name) {
        return switch (name) {
            case "TOSS" -> TOSS;
            case "KAKAO" -> KAKAO;
            default -> throw new IllegalStateException("Unexpected value: " + name);
        };
    }
}
