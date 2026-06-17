package mp.gov.ftms.config;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("events")
public class RabbitConfig {
    @Bean
    public TopicExchange ftmsEventsExchange() {
        return new TopicExchange("ftms.events", true, false);
    }
}

