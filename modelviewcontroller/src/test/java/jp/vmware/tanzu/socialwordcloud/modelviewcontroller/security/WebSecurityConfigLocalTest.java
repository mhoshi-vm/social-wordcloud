package jp.vmware.tanzu.socialwordcloud.modelviewcontroller.security;

import jp.vmware.tanzu.socialwordcloud.library.observability.WfServletSpans;
import jp.vmware.tanzu.socialwordcloud.modelviewcontroller.controller.SocialMessageMQController;
import jp.vmware.tanzu.socialwordcloud.modelviewcontroller.controller.WebSocketEventController;
import jp.vmware.tanzu.socialwordcloud.modelviewcontroller.service.SocialMessageService;
import jp.vmware.tanzu.socialwordcloud.modelviewcontroller.service.SocialMessageTextService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@Import(WebSecurityConfigLocal.class)
class WebSecurityConfigLocalTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private SocialMessageService socialMessageService;

	@MockBean
	private SocialMessageTextService socialMessageTextService;

	// Silent WfServletBean
	@MockBean
	private WfServletSpans wfServletSpans;

	// Silent TweetMQ
	@MockBean
	private SocialMessageMQController socialMessageMQController;

	@MockBean
	private WebSocketEventController webSocketEventController;

	@Test
	void securityFilterChain() throws Exception {
		this.mockMvc.perform(get("/")).andExpect(status().isOk());
		this.mockMvc.perform(get("/login")).andExpect(status().isOk());
		this.mockMvc.perform(get("/api/tweetcount")).andExpect(status().isOk());
		this.mockMvc.perform(get("/livez")).andExpect(status().isOk());
		this.mockMvc.perform(get("/readyz")).andExpect(status().isOk());
		this.mockMvc.perform(get("/tweets")).andExpect(status().is3xxRedirection());
		this.mockMvc.perform(post("/tweetDelete")).andExpect(status().is4xxClientError());
	}

	@Test
	@WithMockUser
	void securityFilterChainAuthenticated() throws Exception {
		this.mockMvc.perform(get("/")).andExpect(status().isOk());
		this.mockMvc.perform(get("/login")).andExpect(status().isOk());
		this.mockMvc.perform(get("/livez")).andExpect(status().isOk());
		this.mockMvc.perform(get("/readyz")).andExpect(status().isOk());
		this.mockMvc.perform(get("/api/tweetcount")).andExpect(status().isOk());
		this.mockMvc.perform(get("/tweets")).andExpect(status().isOk());
	}

}