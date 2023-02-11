package jp.vmware.tanzu.socialwordcloud.library.servicebindings;

import org.springframework.cloud.bindings.Binding;
import org.springframework.cloud.bindings.Bindings;
import org.springframework.cloud.bindings.boot.BindingsPropertiesProcessor;
import org.springframework.core.env.Environment;

import java.util.List;
import java.util.Map;

public class MastodonBindingsPropertiesProcessor implements BindingsPropertiesProcessor {

	public static final String TYPE = "mastodon";

	@Override
	public void process(Environment environment, Bindings bindings, Map<String, Object> properties) {
		if (!environment.getProperty("jp.vmware.tanzu.bindings.boot.mastodon.enable", Boolean.class, true)) {
			return;
		}
		List<Binding> myBindings = bindings.filterBindings(TYPE);
		if (myBindings.size() == 0) {
			return;
		}
		properties.put("mastodon.token", myBindings.get(0).getSecret().get("access-token"));
		properties.put("social.origin", "Mastodon");
	}

}
