package jp.vmware.tanzu.socialwordcloud.library.observability;

import brave.handler.MutableSpan;
import jp.vmware.tanzu.socialwordcloud.library.test_utils.TestSpanHolder;
import jp.vmware.tanzu.socialwordcloud.library.utils.SocialMessageHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = { "test=true" })
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class WfSpansTest {

	@Autowired
	SocialMessageHandler socialMessageHandler;

	@Autowired
	TestRestTemplate testRestTemplate;

	List<MutableSpan> spans;

	@Autowired
	private TestSpanHolder testSpanHolder;

	@LocalServerPort
	private int port;

	@BeforeEach
	void setup() throws InterruptedException {
		testRestTemplate.getForEntity("http://localhost:" + port + "/api/tweetcount", String.class);

	}

	@Test
	void checkATweetHandlerSpans() throws IOException, InterruptedException {
		socialMessageHandler.handle("");

		spans = testSpanHolder.getSpans();

		boolean spanFound = false;

		assertTrue(spans.size() > 0);
		for (MutableSpan span : spans) {
			try {
				if (span.name().startsWith("tweet-stream-handler")) {
					spanFound = true;
					assertEquals("Twitter", span.tag("_outboundExternalService"));
					assertEquals("twitter-api", span.tag("_externalComponent"));
					assertNotNull(span.tag("_externalApplication"));
				}
			}
			catch (Exception ignored) {
			}
		}
		assertTrue(spanFound);
	}

}