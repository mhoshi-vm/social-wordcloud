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
		if (!environment.getProperty("jp.vmware.tanzu.bindings.boot.twitter.enable", Boolean.class, true)) {
			return;
		}
		List<Binding> myBindings = bindings.filterBindings(TYPE);
		if (myBindings.size() == 0) {
			return;
		}

		properties.put("spring.ai.openai.api-key", myBindings.get(0).getSecret().get("api-key"));
		properties.put("spring.ai.openai.model", myBindings.get(0).getSecret().get("model"));
		properties.put("spring.ai.openai.base-url", myBindings.get(0).getSecret().get("url"));
		properties.put("spring.ai.openai.embedding-model", myBindings.get(0).getSecret().get("embedding-models"));

	}

}
