package aop;

public interface IdempotentEventPayload {
    String eventId();
    String topic();
    String topicKey();
}
