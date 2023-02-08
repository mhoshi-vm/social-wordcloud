package jp.vmware.tanzu.socialwordcloud.modelviewcontroller;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "jp.vmware.tanzu.socialwordcloud")
public class ModelViewControllerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ModelViewControllerApplication.class, args);
	}

}
