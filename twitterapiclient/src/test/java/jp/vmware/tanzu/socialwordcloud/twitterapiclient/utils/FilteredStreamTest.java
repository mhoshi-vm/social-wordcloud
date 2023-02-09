package jp.vmware.tanzu.socialwordcloud.twitterapiclient.utils;

import com.twitter.clientlib.ApiException;
import com.twitter.clientlib.model.AddOrDeleteRulesResponse;
import com.twitter.clientlib.model.Rule;
import com.twitter.clientlib.model.RulesLookupResponse;
import jp.vmware.tanzu.socialwordcloud.twitterapiclient.test_utils.TestSocialMessageHandler;
import jp.vmware.tanzu.socialwordcloud.twitterapiclient.test_utils.TestTwitterClient;
import jp.vmware.tanzu.socialwordcloud.library.utils.SocialMessageHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class FilteredStreamTest {

	@Mock
	FilteredStream twitterStream;

	TwitterClient twitterClient;

	SocialMessageHandler socialMessageHandler;

	List<String> hashTags = Arrays.asList("#hoge", "#foo");

	@BeforeEach
	void setup() throws ApiException {
		twitterClient = new TestTwitterClient();
		socialMessageHandler = new TestSocialMessageHandler();

		FilteredStream spyTwitterStream = new FilteredStream(twitterClient, socialMessageHandler, hashTags);

		twitterStream = Mockito.spy(spyTwitterStream);

		AddOrDeleteRulesResponse addOrDeleteRulesResponse = new AddOrDeleteRulesResponse();

		Mockito.doReturn(addOrDeleteRulesResponse).when(twitterStream).addOrDeleteRule(Mockito.any(), Mockito.any());
		Mockito.doReturn(null).when(twitterStream).setInputStream(Mockito.any(), Mockito.any(), Mockito.any());

	}

	@Test
	void testMissingAndUnneededRules() throws ApiException {

		RulesLookupResponse rulesLookupResponse = new RulesLookupResponse();

		List<Rule> rules = new ArrayList<>();

		Rule rule1 = new Rule();
		rule1.setValue("#hoge");
		rules.add(rule1);

		Rule rule2 = new Rule();
		rule2.setValue("#bar");
		rules.add(rule2);

		rulesLookupResponse.setData(rules);

		Mockito.doReturn(rulesLookupResponse).when(twitterStream).getUpstreamRules();

		twitterStream.setTwitterInputStream();

		assertEquals(1, twitterStream.getAddRulesRequest().getAdd().size());
		assertEquals(1, twitterStream.getDeleteRulesRequestDelete().getValues().size());

		assertEquals("#foo", twitterStream.getAddRulesRequest().getAdd().get(0).getValue());
		assertEquals("#bar", twitterStream.getDeleteRulesRequestDelete().getValues().get(0));

	}

	@Test
	void nullRuleResponse() throws ApiException {
		RulesLookupResponse rulesLookupResponse = new RulesLookupResponse();

		List<Rule> rules = new ArrayList<>();
		rulesLookupResponse.setData(rules);

		Mockito.doReturn(rulesLookupResponse).when(twitterStream).getUpstreamRules();

		twitterStream.setTwitterInputStream();

		assertEquals(2, twitterStream.getAddRulesRequest().getAdd().size());

		assertEquals("#hoge", twitterStream.getAddRulesRequest().getAdd().get(0).getValue());
		assertEquals("#foo", twitterStream.getAddRulesRequest().getAdd().get(1).getValue());
	}

}