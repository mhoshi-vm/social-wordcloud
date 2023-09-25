package jp.vmware.tanzu.socialwordcloud.modelviewcontroller.security;

import jp.vmware.tanzu.socialwordcloud.ai_rag.controller.SimpleAiController;
import jp.vmware.tanzu.socialwordcloud.ai_rag.rag.RetriveVectorTable;
import jp.vmware.tanzu.socialwordcloud.modelviewcontroller.controller.RagController;
import jp.vmware.tanzu.socialwordcloud.modelviewcontroller.controller.SocialMessageMQController;
import jp.vmware.tanzu.socialwordcloud.modelviewcontroller.controller.WebSocketEventController;
import jp.vmware.tanzu.socialwordcloud.modelviewcontroller.model.SocialMessage;
import jp.vmware.tanzu.socialwordcloud.modelviewcontroller.service.SocialMessageService;
import jp.vmware.tanzu.socialwordcloud.modelviewcontroller.service.SocialMessageTextService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
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

	@MockBean
	private RetriveVectorTable retriveVectorTable;

	@MockBean
	private RagController ragController;

	@MockBean
	private SimpleAiController simpleAiController;

	@Test
	void securityFilterChainAuthenticated() throws Exception {
		this.mockMvc.perform(get("/").with(oidcLogin())).andExpect(status().isOk());
		this.mockMvc.perform(get("/login").with(oidcLogin())).andExpect(status().isOk());
		this.mockMvc.perform(get("/api/tweetcount").with(oidcLogin())).andExpect(status().isOk());

		Page<SocialMessage> pro = Mockito.mock(Page.class);

		Mockito.when(pro.getContent()).thenReturn(null);
		Mockito.when(pro.getTotalElements()).thenReturn(0L);
		Mockito.when(pro.getTotalPages()).thenReturn(0);

		when(socialMessageService.findAll(anyInt(), anyInt(), any())).thenReturn(pro);
		this.mockMvc.perform(get("/tweets").with(oidcLogin())).andExpect(status().isOk());
	}

}