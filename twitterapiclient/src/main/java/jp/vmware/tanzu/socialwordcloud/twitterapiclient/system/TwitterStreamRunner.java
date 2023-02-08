package jp.vmware.tanzu.socialwordcloud.twitterapiclient.system;

import com.twitter.clientlib.ApiException;
import jp.vmware.tanzu.socialwordcloud.twitterapiclient.utils.TweetSearch;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class TwitterStreamRunner implements CommandLineRunner {

	TweetSearch tweetSearch;

	public TwitterStreamRunner(TweetSearch tweetSearch) {
		this.tweetSearch = tweetSearch;
	}

	@Override
	public void run(String... args) {
		try {
			tweetSearch.actionOnTweetsAsync();
		}
		catch (ApiException e) {
			System.err.println("Status code: " + e.getCode());
			System.err.println("Reason: " + e.getCause());
			System.err.println("Response headers: " + e.getResponseHeaders());
			e.printStackTrace();
		}

	}

}
