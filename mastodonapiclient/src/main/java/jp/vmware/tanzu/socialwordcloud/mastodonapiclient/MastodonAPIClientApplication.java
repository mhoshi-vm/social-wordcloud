package jp.vmware.tanzu.socialwordcloud.mastodonapiclient;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "jp.vmware.tanzu.socialwordcloud")
public class MastodonAPIClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(MastodonAPIClientApplication.class, args);
	}

}
