package jp.vmware.tanzu.socialwordcloud.mastodonapiclient.system;

import jp.vmware.tanzu.socialwordcloud.library.utils.SocialMessageHandler;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Primary
public class DummyHandler implements SocialMessageHandler {

	String output;

	public String getOutput() {
		return output;
	}

	@Override
	public void handle(String tweet) throws IOException, InterruptedException {
		output = tweet;
	}

	public void setOutput(String output) {
		this.output = output;
	}

}
