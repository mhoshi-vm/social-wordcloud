package jp.vmware.tanzu.socialwordcloud.twitterapiclient;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "jp.vmware.tanzu.socialwordcloud")
public class TwitterAPIClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(TwitterAPIClientApplication.class, args);
	}

}
