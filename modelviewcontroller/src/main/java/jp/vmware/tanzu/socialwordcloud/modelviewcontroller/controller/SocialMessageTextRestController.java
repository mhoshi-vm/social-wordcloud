package jp.vmware.tanzu.socialwordcloud.modelviewcontroller.controller;

import jp.vmware.tanzu.socialwordcloud.modelviewcontroller.repository.SocialMessageTextRepository;
import jp.vmware.tanzu.socialwordcloud.modelviewcontroller.service.SocialMessageTextService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class SocialMessageTextRestController {

	private final SocialMessageTextService socialMessageTextService;

	public SocialMessageTextRestController(SocialMessageTextService socialMessageTextService) {
		this.socialMessageTextService = socialMessageTextService;
	}

	@GetMapping("/tweetcount")
	public ResponseEntity<List<SocialMessageTextRepository.TextCount>> listTweetCount() {
		return new ResponseEntity<>(socialMessageTextService.listMessagePage(), HttpStatus.OK);
	}

}
