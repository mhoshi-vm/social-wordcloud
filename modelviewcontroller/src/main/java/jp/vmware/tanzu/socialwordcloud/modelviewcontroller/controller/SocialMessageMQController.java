package jp.vmware.tanzu.socialwordcloud.modelviewcontroller.controller;

import jp.vmware.tanzu.socialwordcloud.modelviewcontroller.service.SocialMessageStreamService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Controller;

@Controller
@ConditionalOnProperty(value = "message.queue.enabled", havingValue = "true")
public class SocialMessageMQController {

	private static final Logger logger = LoggerFactory.getLogger(SocialMessageMQController.class);

	SocialMessageStreamService socialMessageStreamService;

	public SocialMessageMQController(SocialMessageStreamService socialMessageStreamService) {
		this.socialMessageStreamService = socialMessageStreamService;
	}

	@RabbitListener(queues = "${message.queue.queue}")
	public void tweetHandle(String tweet) throws InterruptedException {
		logger.debug("Queue Received : " + tweet);
		try {
			if (!tweet.isEmpty()) {
				logger.debug("Queue Processing");
				socialMessageStreamService.handler(tweet);
			}
		}
		catch (Exception e) {
			logger.warn("Failed processing queue, but skipping");
		}
	}

	@RabbitListener(queues = "#{mvcMQConfiguration.getNotificationQueue()}")
	public void notificationHandle(String tweet) {
		logger.debug("Queue Received : " + tweet);
		try {
			socialMessageStreamService.notifyTweetEvent(tweet);
		}
		catch (Exception e) {
			logger.warn("Failed processing queue, but skipping");
		}

	}

}
