package jp.vmware.tanzu.socialwordcloud.modelviewcontroller.service;

import jp.vmware.tanzu.socialwordcloud.modelviewcontroller.model.SocialMessageText;
import jp.vmware.tanzu.socialwordcloud.modelviewcontroller.repository.SocialMessageTextRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class SocialMessageTextServiceTest {

	@Autowired
	private SocialMessageTextRepository socialMessageTextRepository;

	private SocialMessageTextService socialMessageTextService;

	@BeforeEach
	void setup() {
		socialMessageTextService = new SocialMessageTextService(socialMessageTextRepository);
	}

	@Test
	void listTextCount() {
		SocialMessageText myTweet1 = new SocialMessageText();
		myTweet1.setMessageId("1111");
		myTweet1.setText("Hello");

		socialMessageTextRepository.save(myTweet1);

		SocialMessageText myTweet2 = new SocialMessageText();
		myTweet2.setMessageId("1100");
		myTweet2.setText("Morning");

		socialMessageTextRepository.save(myTweet2);

		SocialMessageText myTweet3 = new SocialMessageText();
		myTweet3.setMessageId("2222");
		myTweet3.setText("Morning");

		socialMessageTextRepository.save(myTweet3);

		SocialMessageText myTweet4 = new SocialMessageText();
		myTweet4.setMessageId("1111");
		myTweet4.setText("Hello");

		socialMessageTextRepository.save(myTweet4);

		SocialMessageText myTweet5 = new SocialMessageText();
		myTweet5.setMessageId("1100");
		myTweet5.setText("Morning");

		socialMessageTextRepository.save(myTweet5);

		SocialMessageText myTweet6 = new SocialMessageText();
		myTweet6.setMessageId("2222");
		myTweet6.setText("Night");

		socialMessageTextRepository.save(myTweet6);

		List<SocialMessageTextRepository.TextCount> textCounts = socialMessageTextService.listTweetsPage();
		assertEquals("Morning", textCounts.get(0).getText());
		assertEquals(3, textCounts.get(0).getSize());
		assertEquals("Hello", textCounts.get(1).getText());
		assertEquals(2, textCounts.get(1).getSize());
		assertEquals("Night", textCounts.get(2).getText());
		assertEquals(1, textCounts.get(2).getSize());
	}

	@Test
	void pageable200Test() {

		for (int i = 0; i < 500; i++) {
			SocialMessageText myTweet = new SocialMessageText();
			myTweet.setMessageId(Integer.toString(i));
			myTweet.setText(Integer.toString(i));
			socialMessageTextRepository.save(myTweet);
		}

		assertEquals(400, socialMessageTextService.listTweetsPage().size());

	}

}