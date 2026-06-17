package mp.gov.ftms.events;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("events")
public class RabbitDomainEventPublisher implements DomainEventPublisher {
    private final RabbitTemplate rabbitTemplate;

    public RabbitDomainEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void publish(String topic, Object event) {
        rabbitTemplate.convertAndSend("ftms.events", topic, event);
    }
}

