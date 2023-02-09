package jp.vmware.tanzu.socialwordcloud.library.test_utils;

import jp.vmware.tanzu.socialwordcloud.library.utils.SocialMessageHandler;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
@Primary
public class TestSocialMessageHandler implements SocialMessageHandler {

	@Override
	public void handle(String tweet) {

	}

}
