package jp.vmware.tanzu.socialwordcloud.twitterapiclient.test_utils;

import com.twitter.clientlib.api.TweetsApi;
import jp.vmware.tanzu.socialwordcloud.twitterapiclient.utils.TwitterClient;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
@Primary
public class TestTwitterClient implements TwitterClient {

	@Override
	public String getStatus() {
		return null;
	}

	@Override
	public void setStatus(String status) {

	}

	@Override
	public TweetsApi getApiInstance() {
		return new TweetsApi();
	}

}
