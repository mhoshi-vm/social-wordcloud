package jp.vmware.tanzu.socialwordcloud.modelviewcontroller.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SocialMessageTest {

	private SocialMessage socialMessage;

	@BeforeEach
	void setUp() {
		socialMessage = new SocialMessage();
		socialMessage.setMessageId("1111");
		socialMessage.setContext("Hello");
		socialMessage.setUsername("James");
	}

	@Test
	void getTweetId() {
		assertEquals("1111", socialMessage.getMessageId());
	}

	@Test
	void setTweetId() {
		socialMessage.setMessageId("2222");
		assertEquals("2222", socialMessage.getMessageId());
	}

	@Test
	void getText() {
		assertEquals("Hello", socialMessage.getContext());
	}

	@Test
	void setText() {
		socialMessage.setContext("Goodbye");
		assertEquals("Goodbye", socialMessage.getContext());
	}

	@Test
	void getUsername() {
		assertEquals("James", socialMessage.getUsername());
	}

	@Test
	void setUsername() {
		socialMessage.setUsername("Robert");
		assertEquals("Robert", socialMessage.getUsername());
	}

}