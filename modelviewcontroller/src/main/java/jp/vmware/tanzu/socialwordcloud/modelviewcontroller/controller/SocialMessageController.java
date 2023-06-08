package jp.vmware.tanzu.socialwordcloud.modelviewcontroller.controller;

import jp.vmware.tanzu.socialwordcloud.modelviewcontroller.model.SocialMessage;
import jp.vmware.tanzu.socialwordcloud.modelviewcontroller.service.SocialMessageService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;

@Controller
public class SocialMessageController {

	public final SocialMessageService socialMessageService;

	public SocialMessageController(SocialMessageService socialMessageService) {
		this.socialMessageService = socialMessageService;
	}

	@GetMapping({ "/tweets" })
	public ModelAndView getAllTweets(@RequestParam(defaultValue = "1") int pageNum,
									 @RequestParam(defaultValue = "100") int pageSize,
									 @RequestParam(defaultValue = "createDateTime") String sortBy) {

		ModelAndView mav = new ModelAndView("tweets");
		int findPageNum = 0;
		if (pageNum > 0) {
			findPageNum = pageNum-1;
		}

		Page<SocialMessage> page = socialMessageService.findAll(findPageNum, pageSize, sortBy);
		List<SocialMessage> socialMessageList = page.getContent();
		mav.addObject("tweets", socialMessageList);
		if (1 <= pageNum && pageNum <= page.getTotalPages()) {
			mav.addObject("currentPage", pageNum);
		}else if (pageNum < 1) {
			mav.addObject("currentPage", 1);
		}else if (page.getTotalPages() == 0){
			mav.addObject("currentPage", 1);
		}else {
			mav.addObject("currentPage",page.getTotalPages());
		}
		mav.addObject("totalPages", page.getTotalPages());
		mav.addObject("totalItems", page.getTotalElements());

		return mav;
	}

	@PostMapping("/tweetDelete")
	public ModelAndView deleteTweet(@ModelAttribute(value = "tweetDel") SocialMessage socialMessage,
									@RequestParam(defaultValue = "1") int pageNum,
									@RequestParam(defaultValue = "100") int pageSize,
									@RequestParam(defaultValue = "createDateTime") String sortBy) {
		socialMessageService.deleteMessage(socialMessage.getMessageId());

		ModelAndView mav = new ModelAndView("tweets");
		int findPageNum = 0;
		if (pageNum > 0) {
			findPageNum = pageNum-1;
		}

		List<SocialMessage> socialMessageList = new ArrayList<>();
		Page<SocialMessage> page = socialMessageService.findAll(findPageNum, pageSize, sortBy);
		if (page.hasContent()) {
			socialMessageList = page.getContent();
		}

		mav.addObject("tweets", socialMessageList);
		if (1 <= pageNum && pageNum <= page.getTotalPages()) {
			mav.addObject("currentPage", pageNum);
		}else if (pageNum < 1) {
			mav.addObject("currentPage", 1);
		}else if (page.getTotalPages() == 0){
			mav.addObject("currentPage", 1);
		}else {
			mav.addObject("currentPage",page.getTotalPages());
		}
		mav.addObject("totalPages", page.getTotalPages());
		mav.addObject("totalItems", page.getTotalElements());

		return mav;
	}

}
