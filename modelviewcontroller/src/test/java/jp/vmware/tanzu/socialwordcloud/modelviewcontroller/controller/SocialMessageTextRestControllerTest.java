package jp.vmware.tanzu.socialwordcloud.modelviewcontroller.controller;

import jp.vmware.tanzu.socialwordcloud.modelviewcontroller.service.SocialMessageTextService;
import jp.vmware.tanzu.socialwordcloud.modelviewcontroller.repository.SocialMessageTextRepository;
import org.hamcrest.Matchers;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SocialMessageTextRestController.class)
@AutoConfigureMockMvc(addFilters = false)
class SocialMessageTextRestControllerTest {

	@MockBean
	private SocialMessageTextService socialMessageTextService;

	@Autowired
	private MockMvc mockMvc;

	// https://medium.com/@reachansari/rest-endpoint-testing-with-mockmvc-7b3da1f83fbb
	@Test
	void listTweetCount() throws Exception {
		CustomTextCount textCount = new CustomTextCount();
		textCount.setText("aaaa");
		textCount.setSize(100L);
		List<SocialMessageTextRepository.TextCount> textCounts = new ArrayList<>();
		textCounts.add(textCount);

		when(socialMessageTextService.listMessagePage()).thenReturn(textCounts);

		mockMvc.perform(get("/api/tweetcount"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$", Matchers.hasSize(1)))
			.andExpect(jsonPath("$[0].text", Matchers.equalTo("aaaa")))
			.andExpect(jsonPath("$[0].size", Matchers.equalTo(100)));
	}

	static class CustomTextCount implements SocialMessageTextRepository.TextCount {

		String text;

		Long size;

		@Override
		public String getText() {
			return this.text;
		}

		public void setText(String text) {
			this.text = text;
		}

		@Override
		public Long getSize() {
			return this.size;
		}

		public void setSize(Long size) {
			this.size = size;
		}

	}

}