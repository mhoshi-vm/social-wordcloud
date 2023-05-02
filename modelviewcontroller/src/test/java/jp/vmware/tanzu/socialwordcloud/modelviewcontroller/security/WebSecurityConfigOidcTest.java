package jp.vmware.tanzu.socialwordcloud.modelviewcontroller.security;

import jp.vmware.tanzu.socialwordcloud.modelviewcontroller.controller.SocialMessageMQController;
import jp.vmware.tanzu.socialwordcloud.modelviewcontroller.controller.WebSocketEventController;
import jp.vmware.tanzu.socialwordcloud.modelviewcontroller.service.SocialMessageService;
import jp.vmware.tanzu.socialwordcloud.modelviewcontroller.service.SocialMessageTextService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oidcLogin;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@Import(WebSecurityConfigLocal.class)
class WebSecurityConfigOidcTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private SocialMessageService socialMessageService;

	@MockBean
	private SocialMessageTextService socialMessageTextService;

	// Silent TweetMQ
	@MockBean
	private SocialMessageMQController socialMessageMQController;

	@MockBean
	private WebSocketEventController webSocketEventController;

	@Test
	void securityFilterChainAuthenticated() throws Exception {
		this.mockMvc.perform(get("/").with(oidcLogin())).andExpect(status().isOk());
		this.mockMvc.perform(get("/login").with(oidcLogin())).andExpect(status().isOk());
		this.mockMvc.perform(get("/api/tweetcount").with(oidcLogin())).andExpect(status().isOk());
		this.mockMvc.perform(get("/tweets").with(oidcLogin())).andExpect(status().isOk());
	}

}