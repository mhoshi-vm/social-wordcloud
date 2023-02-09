package jp.vmware.tanzu.socialwordcloud.modelviewcontroller.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SocialMessageTextTest {

	private SocialMessageText socialMessageText;

	@BeforeEach
	void setUp() {
		socialMessageText = new SocialMessageText();
		socialMessageText.setMessageId("1111");
		socialMessageText.setText("Hello");
	}

	@Test
	void getTweetId() {
		assertEquals("1111", socialMessageText.getMessageId());
	}

	@Test
	void setTweetId() {
		socialMessageText.setMessageId("2222");
		assertEquals("2222", socialMessageText.getMessageId());
	}

	@Test
	void getTxt() {
		assertEquals("Hello", socialMessageText.getText());
	}

	@Test
	void setTxt() {
		socialMessageText.setText("Goodbye");
		assertEquals("Goodbye", socialMessageText.getText());
	}

}