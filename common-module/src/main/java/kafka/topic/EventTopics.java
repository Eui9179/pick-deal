package kafka.topic;

public class EventTopics {
    public final static String ORDER_CANCELED = "order-service.order.cancel";
    public final static String ORDER_COMPLETED = "order-service.order.complete";
    public final static String PAYMENT_DONE = "order-service.payment.done";
    public final static String PAYMENT_FAILED = "order-service.payment.fail";
    public final static String PAYMENT_CANCELED = "order-service.payment.cancel";
    public final static String DEAL_STOCK_RESERVATION_EXPIRED = "store-service.deal.stock-reservation-expired";
}
