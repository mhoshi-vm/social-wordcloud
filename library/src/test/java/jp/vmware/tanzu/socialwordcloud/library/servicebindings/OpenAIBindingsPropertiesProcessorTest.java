package jp.vmware.tanzu.socialwordcloud.library.servicebindings;

import jp.vmware.tanzu.socialwordcloud.library.test_utils.FluentMap;
import org.assertj.core.api.MapAssert;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.bindings.Binding;
import org.springframework.cloud.bindings.Bindings;
import org.springframework.mock.env.MockEnvironment;

import java.nio.file.Paths;
import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static jp.vmware.tanzu.socialwordcloud.library.servicebindings.OpenAIBindingsPropertiesProcessor.TYPE;

class OpenAIBindingsPropertiesProcessorTest {

	private final Bindings bindings = new Bindings(new Binding("test-name", Paths.get("test-path"),
			new FluentMap().withEntry(Binding.TYPE, TYPE)
				.withEntry("api-key", "dummy")
				.withEntry("embedding-models", "dummy-emb-model")
				.withEntry("model", "dummy-model")
				.withEntry("url", "http://dummy")
				.withEntry("vector-table", "dummy_table")));

	private final MockEnvironment environment = new MockEnvironment();

	private final HashMap<String, Object> properties = new HashMap<>();

	@Test
	void test() {
		new OpenAIBindingsPropertiesProcessor().process(environment, bindings, properties);
		assertThat(properties).containsEntry("spring.ai.openai.api-key", "dummy");
		assertThat(properties).containsEntry("spring.ai.openai.embedding-model", "dummy-emb-model");
		assertThat(properties).containsEntry("spring.ai.openai.model", "dummy-model");
		assertThat(properties).containsEntry("spring.ai.openai.base-url", "http://dummy");
		assertThat(properties).containsEntry("openai.vector.table", "dummy_table");
	}

}