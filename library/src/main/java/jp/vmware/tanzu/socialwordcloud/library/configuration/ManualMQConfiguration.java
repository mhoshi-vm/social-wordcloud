package jp.vmware.tanzu.socialwordcloud.library.configuration;

import jakarta.annotation.PostConstruct;
import org.springframework.amqp.rabbit.config.ContainerCustomizer;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class ManualMQConfiguration {
    final RabbitTemplate rabbitTemplate;

    public ManualMQConfiguration(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @PostConstruct
    void setup() {
        this.rabbitTemplate.setObservationEnabled(true);
    }

    @Bean
    ContainerCustomizer<SimpleMessageListenerContainer> containerCustomizer() {
        return (container) -> container.setObservationEnabled(true);
    }
}
