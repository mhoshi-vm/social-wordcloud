package jp.vmware.tanzu.socialwordcloud.modelviewcontroller.service;

import jp.vmware.tanzu.socialwordcloud.modelviewcontroller.model.SocialMessage;
import jp.vmware.tanzu.socialwordcloud.modelviewcontroller.repository.SocialMessageRepository;
import jp.vmware.tanzu.socialwordcloud.modelviewcontroller.repository.SocialMessageTextRepository;
import jp.vmware.tanzu.socialwordcloud.modelviewcontroller.utils.MorphologicalAnalysis;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
@DataJpaTest
class SocialMessageStreamServiceTest {

	private final MorphologicalAnalysis morphologicalAnalysis = new MorphologicalAnalysis();

	SocialMessageStreamService socialMessageStreamService;

	SocialMessageStreamService spySocialMessageStreamService;

	@Autowired
	private SocialMessageRepository socialMessageRepository;

	@Autowired
	private SocialMessageTextRepository socialMessageTextRepository;

	@BeforeEach
	void setup() {
		this.socialMessageStreamService = new SocialMessageStreamService(socialMessageRepository,
				socialMessageTextRepository, morphologicalAnalysis, "ja", "postgres");

		this.spySocialMessageStreamService = Mockito.spy(socialMessageStreamService);

	}

	@Test
	void normalCase() throws IOException, InterruptedException {

		SocialMessage socialMessage = new SocialMessage();
		socialMessage.setMessageId("111");
		socialMessage.setContext("This is test tweet");
		socialMessage.setLang("ja");
		socialMessage.setUsername("Jannie");

		Mockito.doReturn(socialMessage).when(spySocialMessageStreamService).setSocialMessage(Mockito.any());

		spySocialMessageStreamService.handler("this is test");

		List<SocialMessage> socialMessages = socialMessageRepository.findAllByOrderByMessageIdDesc();
		assertEquals(1, socialMessages.size());

		List<SocialMessageTextRepository.TextCount> textCounts = socialMessageTextRepository
			.listTextCount(Pageable.ofSize(10));
		assertEquals("This", textCounts.get(0).getText());
		assertEquals(1, textCounts.get(0).getSize());
		assertEquals("is", textCounts.get(1).getText());
		assertEquals(1, textCounts.get(1).getSize());
		assertEquals("test", textCounts.get(2).getText());
		assertEquals(1, textCounts.get(2).getSize());
		assertEquals("tweet", textCounts.get(3).getText());
		assertEquals(1, textCounts.get(3).getSize());
	}

	@Test
	void returnWhenLineIsEmpty() throws IOException, InterruptedException {
		spySocialMessageStreamService.handler("");

		List<SocialMessage> socialMessages = socialMessageRepository.findAllByOrderByMessageIdDesc();
		assertEquals(0, socialMessages.size());

		List<SocialMessageTextRepository.TextCount> textCounts = socialMessageTextRepository
			.listTextCount(Pageable.ofSize(10));
		assertEquals(0, textCounts.size());
	}

	@Test
	void doNothingOnNonJson() throws IOException, InterruptedException {
		spySocialMessageStreamService.handler("dd");

		List<SocialMessage> socialMessages = socialMessageRepository.findAllByOrderByMessageIdDesc();
		assertEquals(0, socialMessages.size());

		List<SocialMessageTextRepository.TextCount> textCounts = socialMessageTextRepository
			.listTextCount(Pageable.ofSize(10));
		assertEquals(0, textCounts.size());
	}

	@Test
	void skipHashTagAndUsername() throws IOException, InterruptedException {

		SocialMessage socialMessage = new SocialMessage();
		socialMessage.setMessageId("111");
		socialMessage.setContext("#hoge_foo #foo_bar This is !$ test tweet");
		socialMessage.setLang("ja");
		socialMessage.setUsername("Jannie");

		Mockito.doReturn(socialMessage).when(spySocialMessageStreamService).setSocialMessage(Mockito.any());

		spySocialMessageStreamService.handler("this is test");

		List<SocialMessage> socialMessages = socialMessageRepository.findAllByOrderByMessageIdDesc();
		assertEquals(1, socialMessages.size());

		List<SocialMessageTextRepository.TextCount> textCounts = socialMessageTextRepository
			.listTextCount(Pageable.ofSize(10));
		assertEquals("This", textCounts.get(0).getText());
		assertEquals(1, textCounts.get(0).getSize());
		assertEquals("is", textCounts.get(1).getText());
		assertEquals(1, textCounts.get(1).getSize());
		assertEquals("test", textCounts.get(2).getText());
		assertEquals(1, textCounts.get(2).getSize());
		assertEquals("tweet", textCounts.get(3).getText());
		assertEquals(1, textCounts.get(3).getSize());
	}

	/*
	 * @Test void skipNonJapanese() throws InterruptedException {
	 *
	 * Tweet dummyTweet = new Tweet(); dummyTweet.setId("111");
	 * dummyTweet.setText("This is test tweet"); dummyTweet.setLang("en");
	 *
	 * User dummyUser = new User(); dummyUser.setUsername("Jannie"); List<User> dummyUsers
	 * = new ArrayList<>(); dummyUsers.add(dummyUser);
	 *
	 * Expansions expansions = new Expansions(); expansions.setUsers(dummyUsers);
	 *
	 * StreamingTweetResponse streamingTweetResponse = new StreamingTweetResponse();
	 * streamingTweetResponse.setData(dummyTweet);
	 * streamingTweetResponse.setIncludes(expansions);
	 *
	 * Mockito.doReturn(streamingTweetResponse).when(spyTweetStreamService).
	 * setStreamTweetResponse(Mockito.any());
	 *
	 * spyTweetStreamService.handler("this is test");
	 *
	 * List<MyTweet> myTweets = myTweetRepository.findAllByOrderByTweetIdDesc();
	 * assertEquals(0, myTweets.size());
	 *
	 * }
	 *
	 * @Test void englishSupport() throws InterruptedException {
	 *
	 * this.tweetStreamService = new TweetStreamService(myTweetRepository,
	 * tweetTextRepository, morphologicalAnalysis, "en");
	 *
	 * this.spyTweetStreamService = Mockito.spy(tweetStreamService);
	 *
	 * Tweet dummyTweet = new Tweet(); dummyTweet.setId("111");
	 * dummyTweet.setText("This is test tweet"); dummyTweet.setLang("en");
	 *
	 * User dummyUser = new User(); dummyUser.setUsername("Jannie"); List<User> dummyUsers
	 * = new ArrayList<>(); dummyUsers.add(dummyUser);
	 *
	 * Expansions expansions = new Expansions(); expansions.setUsers(dummyUsers);
	 *
	 * StreamingTweetResponse streamingTweetResponse = new StreamingTweetResponse();
	 * streamingTweetResponse.setData(dummyTweet);
	 * streamingTweetResponse.setIncludes(expansions);
	 *
	 * Mockito.doReturn(streamingTweetResponse).when(spyTweetStreamService).
	 * setStreamTweetResponse(Mockito.any());
	 *
	 * spyTweetStreamService.handler("this is test");
	 *
	 * List<MyTweet> myTweets = myTweetRepository.findAllByOrderByTweetIdDesc();
	 * assertEquals(1, myTweets.size());
	 *
	 * }
	 */

}