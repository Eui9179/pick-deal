package exception;

public class NotSupportedOrderStatus extends RuntimeException {
    public NotSupportedOrderStatus(String message) {
        super(message);
    }
}
