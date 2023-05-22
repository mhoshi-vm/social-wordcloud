package jp.vmware.tanzu.socialwordcloud.modelviewcontroller.controller;

import jp.vmware.tanzu.socialwordcloud.modelviewcontroller.model.SocialMessage;
import jp.vmware.tanzu.socialwordcloud.modelviewcontroller.service.SocialMessageService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class SocialMessageController {

	public final SocialMessageService socialMessageService;

	public SocialMessageController(SocialMessageService socialMessageService) {
		this.socialMessageService = socialMessageService;
	}

	@GetMapping({ "/tweets" })
	public ModelAndView getAllTweets() {
		ModelAndView mav = new ModelAndView("tweets");
		mav.addObject("tweets", socialMessageService.findAllByOrderByMessageIdDesc());

		return mav;
	}

	@PostMapping("/tweetDelete")
	public ModelAndView deleteTweet(@ModelAttribute(value = "tweetDel") SocialMessage socialMessage) {
		socialMessageService.deleteMessage(socialMessage.getMessageId());
		ModelAndView mav = new ModelAndView("tweets");
		mav.addObject("tweets", socialMessageService.findAllByOrderByMessageIdDesc());
		return mav;
	}

}
