package jp.vmware.tanzu.socialwordcloud.library.servicebindings;

import org.springframework.cloud.bindings.Binding;
import org.springframework.cloud.bindings.Bindings;
import org.springframework.cloud.bindings.boot.BindingsPropertiesProcessor;
import org.springframework.core.env.Environment;

import java.util.List;
import java.util.Map;

public class OpenAIBindingsPropertiesProcessor implements BindingsPropertiesProcessor {

	public static final String TYPE = "openai";

	@Override
	public void process(Environment environment, Bindings bindings, Map<String, Object> properties) {
		if (!environment.getProperty("jp.vmware.tanzu.bindings.boot.openai.enable", Boolean.class, true)) {
			return;
		}
		List<Binding> myBindings = bindings.filterBindings(TYPE);
		if (myBindings.isEmpty()) {
			return;
		}

		properties.put("spring.ai.openai.api-key", myBindings.get(0).getSecret().get("api-key"));
		properties.put("spring.ai.openai.chat.options.model", myBindings.get(0).getSecret().get("model"));
		properties.put("spring.ai.openai.base-url", myBindings.get(0).getSecret().get("url"));
		properties.put("spring.ai.openai.embedding.options.model",
				myBindings.get(0).getSecret().get("embedding-models"));
		properties.put("openai.vector.table", myBindings.get(0).getSecret().get("vector-table"));

	}

}
