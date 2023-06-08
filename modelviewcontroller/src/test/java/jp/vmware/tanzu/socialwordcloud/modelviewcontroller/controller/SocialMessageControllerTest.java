package jp.vmware.tanzu.socialwordcloud.modelviewcontroller.controller;

import jp.vmware.tanzu.socialwordcloud.modelviewcontroller.repository.SocialMessageRepository;
import jp.vmware.tanzu.socialwordcloud.modelviewcontroller.service.SocialMessageService;
import jp.vmware.tanzu.socialwordcloud.modelviewcontroller.model.SocialMessage;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
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

		Page<SocialMessage> pro= Mockito.mock(Page.class);

		Mockito.when(pro.getContent()).thenReturn(socialMessageList);
		Mockito.when(pro.getTotalElements()).thenReturn(2L);
		Mockito.when(pro.getTotalPages()).thenReturn(1);

		when(socialMessageService.findAll(anyInt(),anyInt(),any())).thenReturn(pro);

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

		SocialMessage socialMessage2 = new SocialMessage();
		socialMessage2.setMessageId("2222");
		socialMessage2.setContext("Hello");
		socialMessage2.setUsername("Jane");

		List<SocialMessage> socialMessageList = new ArrayList<>();

		socialMessageList.add(socialMessage2);

		Page<SocialMessage> pro= Mockito.mock(Page.class);

		Mockito.when(pro.getContent()).thenReturn(socialMessageList);
		Mockito.when(pro.getTotalElements()).thenReturn(1L);
		Mockito.when(pro.getTotalPages()).thenReturn(1);

		when(socialMessageService.findAll(anyInt(),anyInt(),any())).thenReturn(pro);

		mockMvc.perform(post("/tweetDelete").flashAttr("tweetDel", socialMessage1)).andExpect(status().isOk());
	}

}