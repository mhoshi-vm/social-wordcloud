package jp.vmware.tanzu.socialwordcloud.library.servicebindings;

import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.cloud.bindings.Binding;
import org.springframework.cloud.bindings.Bindings;
import org.springframework.cloud.bindings.boot.BindingsPropertiesProcessor;
import org.springframework.core.env.Environment;

import java.util.List;
import java.util.Map;

public class WfEnableOnlyViaBindings implements BindingsPropertiesProcessor {

	public static final String TYPE = "wavefront";

	@Override
	public void process(Environment environment, Bindings bindings, Map<String, Object> map) {

		if (!environment.getProperty("jp.vmware.tanzu.bindings.boot.wavefront.enable", Boolean.class, true)) {
			return;
		}

		List<Binding> wfBindings = bindings.filterBindings(TYPE);
		if (wfBindings.size() == 0) {
			map.put("management.endpoint.wavefront.enabled", "false");
			map.put("management.wavefront.metrics.export.enabled", "false");
			map.put("management.wavefront.api-token", "dummy");
			map.put("management.tracing.enabled", "false");
		}
		else {
			map.put("management.tracing.sampling.probability", "1.0");
			map.put("management.wavefront.api-token", wfBindings.get(0).getSecret().get("api-token"));
			map.put("management.wavefront.uri", wfBindings.get(0).getSecret().get("uri"));
		}
	}

	static class BindingPropertiesMapper {

		private final Map<String, String> secret;

		private final Map<String, Object> properties;

		private final PropertyMapper mapper;

		public BindingPropertiesMapper(Map<String, String> secret, Map<String, Object> properties) {
			this.secret = secret;
			this.properties = properties;
			this.mapper = PropertyMapper.get().alwaysApplyingWhenNonNull();
		}

		public void map(String from, String to) {
			mapper.from(secret.get(from)).to(value -> this.properties.put(to, value));
		}

	}

}
