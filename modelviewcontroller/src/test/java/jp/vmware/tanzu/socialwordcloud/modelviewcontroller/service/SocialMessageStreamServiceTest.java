package jp.vmware.tanzu.socialwordcloud.modelviewcontroller.service;

import jp.vmware.tanzu.socialwordcloud.ai_rag.rag.RetriveVectorTable;
import jp.vmware.tanzu.socialwordcloud.modelviewcontroller.model.SocialMessage;
import jp.vmware.tanzu.socialwordcloud.modelviewcontroller.model.SocialMessageImage;
import jp.vmware.tanzu.socialwordcloud.modelviewcontroller.repository.SocialMessageImageReposity;
import jp.vmware.tanzu.socialwordcloud.modelviewcontroller.repository.SocialMessageRepository;
import jp.vmware.tanzu.socialwordcloud.modelviewcontroller.repository.SocialMessageTextRepository;
import jp.vmware.tanzu.socialwordcloud.modelviewcontroller.utils.MorphologicalAnalysis;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import org.springframework.util.ResourceUtils;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

	@Autowired
	private SocialMessageImageReposity socialMessageImageReposity;

	@Mock
	public RetriveVectorTable retriveVectorTable;

	@BeforeEach
	void setup() {
		this.socialMessageStreamService = new SocialMessageStreamService(socialMessageRepository,
				socialMessageTextRepository, socialMessageImageReposity, morphologicalAnalysis, retriveVectorTable,
				"ja", "postgres");

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

		List<SocialMessage> socialMessages = socialMessageRepository.findAll();
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

		List<SocialMessage> socialMessages = socialMessageRepository.findAll();
		assertEquals(0, socialMessages.size());

		List<SocialMessageTextRepository.TextCount> textCounts = socialMessageTextRepository
			.listTextCount(Pageable.ofSize(10));
		assertEquals(0, textCounts.size());
	}

	@Test
	void doNothingOnNonJson() throws IOException, InterruptedException {
		spySocialMessageStreamService.handler("dd");

		List<SocialMessage> socialMessages = socialMessageRepository.findAll();
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

		List<SocialMessage> socialMessages = socialMessageRepository.findAll();
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
	void testImage() throws IOException, InterruptedException {
		SocialMessage socialMessage = new SocialMessage();
		socialMessage.setMessageId("111");
		socialMessage.setContext("#hoge_foo #foo_bar This is !$ test tweet");
		socialMessage.setLang("ja");
		socialMessage.setUsername("Jannie");

		List<SocialMessageImage> socialMessageImages = new ArrayList<>();
		SocialMessageImage socialMessageImage = new SocialMessageImage();
		File initialFile = ResourceUtils.getFile("classpath:test.png");
		InputStream targetStream = new DataInputStream(new FileInputStream(initialFile));
		byte[] orginalImg = Files.readAllBytes(initialFile.toPath());
		socialMessageImage.setMessageId("111");
		socialMessageImage.setImage(orginalImg);
		socialMessageImages.add(socialMessageImage);

		Mockito.doReturn(socialMessage).when(spySocialMessageStreamService).setSocialMessage(Mockito.any());
		Mockito.doReturn(socialMessageImages).when(spySocialMessageStreamService).setImage(Mockito.any());

		spySocialMessageStreamService.handler("this is test");

		List<SocialMessageImage> socialMessageImageReposityAll = socialMessageImageReposity.findAll();
		assertEquals(socialMessageImageReposityAll.size(), 1);
		assertTrue(Arrays.equals(orginalImg, socialMessageImageReposityAll.get(0).getImage()));

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