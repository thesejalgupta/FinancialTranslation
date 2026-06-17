package mp.gov.ftms.events;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("!events")
public class InMemoryDomainEventPublisher implements DomainEventPublisher {
    private static final Logger log = LoggerFactory.getLogger(InMemoryDomainEventPublisher.class);

    @Override
    public void publish(String topic, Object event) {
        log.info("Mock event published topic={} payload={}", topic, event);
    }
}

