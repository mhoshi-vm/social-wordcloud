package jp.vmware.tanzu.socialwordcloud.library.utils;

import io.micrometer.observation.annotation.Observed;
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

	public String exchangeName;

	RabbitTemplate rabbitTemplate;

	public SocialMessageHandlerMQ(RabbitTemplate rabbitTemplate,
			@Value("${message.queue.exchange}") String exchangeName) {
		this.rabbitTemplate = rabbitTemplate;
		this.exchangeName = exchangeName;
	}

	@Override
	public void handle(String tweet) {
		logger.debug("Queue Arrived:" + tweet);
		if (!tweet.isEmpty()) {
			logger.debug("Queue Sent:" + tweet);
			this.rabbitTemplate.convertAndSend(exchangeName, "", tweet);
		}
	}

}
