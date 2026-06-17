package mp.gov.ftms.events;

public interface DomainEventPublisher {
    void publish(String topic, Object event);
}

