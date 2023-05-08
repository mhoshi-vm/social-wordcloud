package jp.vmware.tanzu.socialwordcloud.modelviewcontroller.controller;

import jp.vmware.tanzu.socialwordcloud.modelviewcontroller.service.SocialMessageService;
import jp.vmware.tanzu.socialwordcloud.modelviewcontroller.model.SocialMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.Objects;

@Controller
public class SocialMessageController {

	public final SocialMessageService socialMessageService;

	public final String database;

	public SocialMessageController(SocialMessageService socialMessageService, @Value("${database}") String database) {
		this.socialMessageService = socialMessageService;
		this.database = database;
	}

	@GetMapping({ "/tweets" })
	public ModelAndView getAllTweets() {
		ModelAndView mav = new ModelAndView("tweets");
		if (Objects.equals(database, "greenplum")) {
			mav.addObject("tweets", socialMessageService.findAllByOrderByMessageIdDescSentiment());
		}
		else {
			mav.addObject("tweets", socialMessageService.findAllByOrderByMessageIdDesc());
		}
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
