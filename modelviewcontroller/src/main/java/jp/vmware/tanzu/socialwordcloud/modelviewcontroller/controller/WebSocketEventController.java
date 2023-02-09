package jp.vmware.tanzu.socialwordcloud.modelviewcontroller.controller;

import jp.vmware.tanzu.socialwordcloud.modelviewcontroller.service.SocialMessageStreamService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@Controller
@RequestMapping("/api")
public class WebSocketEventController {

	SocialMessageStreamService socialMessageStreamService;

	public WebSocketEventController(SocialMessageStreamService socialMessageStreamService) {
		this.socialMessageStreamService = socialMessageStreamService;
	}

	@RequestMapping("/tweetEvent")
	public SseEmitter newTweet() {
		SseEmitter sseEmitter = new SseEmitter(-1L);
		List<SseEmitter> emitters = socialMessageStreamService.getEmitters();
		emitters.add(sseEmitter);
		sseEmitter.onCompletion(() -> emitters.remove(sseEmitter));

		return sseEmitter;
	}

}
