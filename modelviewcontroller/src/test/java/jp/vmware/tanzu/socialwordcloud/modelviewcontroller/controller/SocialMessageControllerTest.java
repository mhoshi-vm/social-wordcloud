package jp.vmware.tanzu.socialwordcloud.modelviewcontroller.controller;

import jp.vmware.tanzu.socialwordcloud.modelviewcontroller.service.SocialMessageService;
import jp.vmware.tanzu.socialwordcloud.modelviewcontroller.model.SocialMessage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SocialMessageController.class)
@AutoConfigureMockMvc(addFilters = false)
class SocialMessageControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private SocialMessageService socialMessageService;

	@Test
	void getAllTweets() throws Exception {
		SocialMessage socialMessage1 = new SocialMessage();
		socialMessage1.setMessageId("1111");
		socialMessage1.setContext("Hello");
		socialMessage1.setUsername("James");

		SocialMessage socialMessage2 = new SocialMessage();
		socialMessage2.setMessageId("2222");
		socialMessage2.setContext("Hello");
		socialMessage2.setUsername("Jane");

		List<SocialMessage> socialMessageList = new ArrayList<>();

		socialMessageList.add(socialMessage1);
		socialMessageList.add(socialMessage2);

		when(socialMessageService.findAllByOrderByCreateDateTimeDesc()).thenReturn(socialMessageList);

		mockMvc.perform(get("/tweets"))
			.andExpect(status().isOk())
			.andExpect(view().name("tweets"))
			.andExpect(model().attribute("tweets", socialMessageList));
	}

	@Test
	void deleteTweet() throws Exception {

		SocialMessage socialMessage1 = new SocialMessage();
		socialMessage1.setMessageId("1111");
		socialMessage1.setContext("Hello");
		socialMessage1.setUsername("James");

		List<SocialMessage> socialMessageList = new ArrayList<>();

		socialMessageList.add(socialMessage1);

		when(socialMessageService.findAllByOrderByCreateDateTimeDesc()).thenReturn(null);

		mockMvc.perform(post("/tweetDelete").flashAttr("tweetDel", socialMessage1)).andExpect(status().isOk());
	}

}