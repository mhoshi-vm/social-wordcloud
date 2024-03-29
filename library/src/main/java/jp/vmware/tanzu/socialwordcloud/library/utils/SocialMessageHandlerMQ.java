package jp.vmware.tanzu.socialwordcloud.library.utils;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import io.micrometer.observation.annotation.Observed;
import io.micrometer.tracing.Tracer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(value = "message.queue.enabled", havingValue = "true")
public class SocialMessageHandlerMQ implements SocialMessageHandler {

	private static final Logger logger = LoggerFactory.getLogger(SocialMessageHandlerMQ.class);

	public final Tracer tracer;

	public final ObservationRegistry observationRegistry;

	public String exchangeName;

	RabbitTemplate rabbitTemplate;

	public SocialMessageHandlerMQ(Tracer tracer, ObservationRegistry observationRegistry, RabbitTemplate rabbitTemplate,
			@Value("${message.queue.exchange}") String exchangeName) {
		this.rabbitTemplate = rabbitTemplate;
		this.observationRegistry = observationRegistry;
		this.tracer = tracer;
		this.exchangeName = exchangeName;
	}

	@Override
	@Observed(name = "handle-social-message-rabbitmq", contextualName = "handle-social-message-rabbitmq")
	public void handle(String tweet) {
		logger.debug("Queue Arrived:" + tweet);
		if (!tweet.isEmpty()) {
			Observation.createNotStarted("rabbit-producer", this.observationRegistry).observe(() -> {
				logger.debug("Queue Sent:" + tweet);
				this.rabbitTemplate.convertAndSend(exchangeName, "", tweet);
			});
		}
	}

}
