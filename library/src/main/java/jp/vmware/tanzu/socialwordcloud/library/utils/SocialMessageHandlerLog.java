package jp.vmware.tanzu.socialwordcloud.library.utils;

import io.micrometer.observation.annotation.Observed;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(value = "message.queue.enabled", havingValue = "false", matchIfMissing = true)
public class SocialMessageHandlerLog implements SocialMessageHandler {

	@Override
	@Observed(name = "handle-social-message", contextualName = "handle-social-message")
	public void handle(String tweet) {
		System.out.println(tweet);
	}

}
