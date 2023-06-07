package jp.vmware.tanzu.socialwordcloud.library.observability;

import io.lettuce.core.resource.ClientResources;
import io.micrometer.observation.ObservationRegistry;
import io.micrometer.observation.aop.ObservedAspect;
import jakarta.annotation.PostConstruct;
import org.springframework.amqp.rabbit.config.ContainerCustomizer;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.observability.MicrometerTracingAdapter;

@Configuration(proxyBeanMethods = false)
class ObserveConfiguration {

	// To have the @Observed support we need to register this aspect
	@Bean
	ObservedAspect observedAspect(ObservationRegistry observationRegistry) {
		return new ObservedAspect(observationRegistry);
	}

	final RabbitTemplate rabbitTemplate;

	public ObserveConfiguration(RabbitTemplate rabbitTemplate) {
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

	@Bean
	public ClientResources clientResources(ObservationRegistry observationRegistry) {

		return ClientResources.builder()
				.tracing(new MicrometerTracingAdapter(observationRegistry, "my-redis-cache"))
				.build();
	}

	@Bean
	public LettuceConnectionFactory lettuceConnectionFactory(ClientResources clientResources) {

		LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()
				.clientResources(clientResources).build();
		RedisConfiguration redisConfiguration = new RedisConfiguration() {
		};
		return new LettuceConnectionFactory(redisConfiguration, clientConfig);
	}
}