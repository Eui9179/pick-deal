package kafka.topic;

public class EventTopics {
    public final static String ORDER_CANCELED = "order-service.order.cancel";
    public final static String ORDER_COMPLETED = "order-service.order.complete";
    public final static String PAYMENT_APPROVED = "order-service.payment.approve";
    public final static String PAYMENT_APPROVED_FAIL = "order-service.payment.fail";
    public final static String PAYMENT_CANCELED = "order-service.payment.cancel";
    public final static String DEAL_STOCK_RESERVATION_EXPIRED = "store-service.deal.stock-reservation-expired";
    public final static String DEAL_STOCK_COMMIT = "store-service.deal.stock-commit";
    public final static String DEAL_STOCK_COMMIT_FAIL = "store-service.deal.stock-commit-fail";
    public final static String USER_POINT_APPLIED = "user-service.handle-point";
    public final static String USER_POINT_APPLIED_FAIL = "user-service.handle-point-fail";
}
