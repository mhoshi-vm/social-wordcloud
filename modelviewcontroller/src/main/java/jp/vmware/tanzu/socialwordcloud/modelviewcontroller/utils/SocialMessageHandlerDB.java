package jp.vmware.tanzu.socialwordcloud.modelviewcontroller.utils;

import io.micrometer.observation.annotation.Observed;
import jp.vmware.tanzu.socialwordcloud.library.utils.SocialMessageHandler;
import jp.vmware.tanzu.socialwordcloud.modelviewcontroller.service.SocialMessageStreamService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@ConditionalOnProperty(value = "message.queue.enabled", havingValue = "false", matchIfMissing = true)
@Primary
public class SocialMessageHandlerDB implements SocialMessageHandler {

	SocialMessageStreamService socialMessageStreamService;

	public SocialMessageHandlerDB(SocialMessageStreamService socialMessageStreamService) {
		this.socialMessageStreamService = socialMessageStreamService;
	}

	@Override
	@Observed(name = "handle-social-message", contextualName = "handle-social-message")
	public void handle(String tweet) throws IOException, InterruptedException {
		socialMessageStreamService.handler(tweet);
		socialMessageStreamService.notifyTweetEvent(tweet);
	}

}
