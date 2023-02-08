package jp.vmware.tanzu.socialwordcloud.modelviewcontroller.service;

import jp.vmware.tanzu.socialwordcloud.modelviewcontroller.utils.MorphologicalAnalysis;
import jp.vmware.tanzu.socialwordcloud.modelviewcontroller.repository.MyTweetRepository;
import jp.vmware.tanzu.socialwordcloud.modelviewcontroller.repository.TweetTextRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@ExtendWith(MockitoExtension.class)
@DataJpaTest
class TweetStreamServiceRollbackTest {

	private final MorphologicalAnalysis morphologicalAnalysis = new MorphologicalAnalysis();

	TweetStreamService tweetStreamService;

	TweetStreamService spyTweetStreamService;

	@Autowired
	private MyTweetRepository myTweetRepository;

	@Mock
	private TweetTextRepository tweetTextRepository;

	private TweetTextRepository spyTweetTextRepository;

	// https://stackoverflow.com/questions/58443290/why-doesnt-my-transactional-method-rollback-when-testing

	// @BeforeEach
	// void setup() throws IOException {
	//
	// this.tweetStreamService = new TweetStreamService(myTweetRepository,
	// spyTweetTextRepository,
	// morphologicalAnalysis);
	//
	// this.spyTweetStreamService = Mockito.spy(tweetStreamService);
	//
	// Tweet dummyTweet = new Tweet();
	// dummyTweet.setId("111");
	// dummyTweet.setText("#hoge_foo #foo_bar This is !$ test tweet");
	//
	// User dummyUser = new User();
	// dummyUser.setUsername("Jannie");
	//
	// List<User> dummyUsers = new ArrayList<>();
	// dummyUsers.add(dummyUser);
	//
	// Expansions expansions = new Expansions();
	// expansions.setUsers(dummyUsers);
	//
	// StreamingTweetResponse streamingTweetResponse = new StreamingTweetResponse();
	// streamingTweetResponse.setData(dummyTweet);
	// streamingTweetResponse.setIncludes(expansions);
	//
	// Mockito.doReturn(streamingTweetResponse).when(spyTweetStreamService).setStreamTweetResponse(Mockito.any());
	// }
	//
	//
	// @Test
	// void rollbackTest() {
	//
	// Mockito.when(tweetTextRepository.save(Mockito.any())).thenThrow(new
	// RuntimeException());
	//
	// try {
	// spyTweetStreamService.handler("this is test");
	// } catch (Exception e) {
	// }
	//
	// List<MyTweet> myTweets = myTweetRepository.findAllByOrderByTweetIdDesc();
	// assertEquals(0, myTweets.size());
	// }

}