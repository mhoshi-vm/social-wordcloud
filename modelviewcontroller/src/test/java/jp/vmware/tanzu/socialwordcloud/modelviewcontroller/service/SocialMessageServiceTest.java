package jp.vmware.tanzu.socialwordcloud.modelviewcontroller.service;

import jp.vmware.tanzu.socialwordcloud.modelviewcontroller.model.SocialMessageText;
import jp.vmware.tanzu.socialwordcloud.modelviewcontroller.model.SocialMessage;
import jp.vmware.tanzu.socialwordcloud.modelviewcontroller.repository.SocialMessageRepository;
import jp.vmware.tanzu.socialwordcloud.modelviewcontroller.repository.SocialMessageTextRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class SocialMessageServiceTest {

	@Autowired
	private SocialMessageRepository socialMessageRepository;

	@Autowired
	private SocialMessageTextRepository socialMessageTextRepository;

	private SocialMessageService socialMessageService;

	@BeforeEach
	void setup() {
		socialMessageService = new SocialMessageService(socialMessageRepository, socialMessageTextRepository);
	}

	@Test
	void findAllByOrderByCreateDateTimeDesc() {

		SocialMessage socialMessage1 = new SocialMessage();
		socialMessage1.setMessageId("1111");
		socialMessage1.setContext("Hello");
		socialMessage1.setUsername("James");

		socialMessageRepository.save(socialMessage1);

		SocialMessage socialMessage2 = new SocialMessage();
		socialMessage2.setMessageId("1100");
		socialMessage2.setContext("Morning");
		socialMessage2.setUsername("Jane");

		socialMessageRepository.save(socialMessage2);

		SocialMessage socialMessage3 = new SocialMessage();
		socialMessage3.setMessageId("2222");
		socialMessage3.setContext("Night");
		socialMessage3.setUsername("Ryan");

		socialMessageRepository.save(socialMessage3);

		Page<SocialMessage> socialMessages = socialMessageService.findAll(0,100,"createDateTime");
		assertEquals("2222", socialMessages.getContent().get(0).getMessageId());
		assertEquals("1100", socialMessages.getContent().get(1).getMessageId());
		assertEquals("1111", socialMessages.getContent().get(2).getMessageId());
	}

	@Test
	void deleteTweetId() {
		SocialMessage socialMessage1 = new SocialMessage();
		socialMessage1.setMessageId("1111");
		socialMessage1.setContext("Hello");
		socialMessage1.setUsername("James");

		socialMessageRepository.save(socialMessage1);

		SocialMessage socialMessage2 = new SocialMessage();
		socialMessage2.setMessageId("1100");
		socialMessage2.setContext("Morning");
		socialMessage2.setUsername("Jane");

		socialMessageRepository.save(socialMessage2);

		SocialMessage socialMessage3 = new SocialMessage();
		socialMessage3.setMessageId("2222");
		socialMessage3.setContext("Night");
		socialMessage3.setUsername("Ryan");

		socialMessageRepository.save(socialMessage3);

		SocialMessageText socialMessageText1 = new SocialMessageText();
		socialMessageText1.setMessageId("1111");
		socialMessageText1.setText("Hello");

		socialMessageTextRepository.save(socialMessageText1);

		SocialMessageText socialMessageText2 = new SocialMessageText();
		socialMessageText2.setMessageId("1100");
		socialMessageText2.setText("Morning");

		socialMessageTextRepository.save(socialMessageText2);

		SocialMessageText socialMessageText3 = new SocialMessageText();
		socialMessageText3.setMessageId("2222");
		socialMessageText3.setText("Morning");

		socialMessageTextRepository.save(socialMessageText3);

		SocialMessageText socialMessageText4 = new SocialMessageText();
		socialMessageText4.setMessageId("1111");
		socialMessageText4.setText("Hello");

		socialMessageTextRepository.save(socialMessageText4);

		socialMessageService.deleteMessage("1111");

		List<SocialMessage> socialMessages = (List<SocialMessage>) socialMessageRepository.findAll();
		List<SocialMessageText> socialMessageTexts = (List<SocialMessageText>) socialMessageTextRepository.findAll();

		assertEquals(2, socialMessages.size());
		assertEquals(2, socialMessageTexts.size());

	}

}