package jp.vmware.tanzu.socialwordcloud.modelviewcontroller.utils;

import jp.vmware.tanzu.socialwordcloud.library.utils.TweetHandler;
import jp.vmware.tanzu.socialwordcloud.modelviewcontroller.service.TweetStreamService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@ConditionalOnProperty(value = "message.queue.enabled", havingValue = "false", matchIfMissing = true)
@Primary
public class TweetHandlerDB implements TweetHandler {

	TweetStreamService tweetStreamService;

	public TweetHandlerDB(TweetStreamService tweetStreamService) {
		this.tweetStreamService = tweetStreamService;
	}

	@Override
	public void handle(String tweet) throws IOException, InterruptedException {
		tweetStreamService.handler(tweet);
		tweetStreamService.notifyTweetEvent(tweet);
	}

}
