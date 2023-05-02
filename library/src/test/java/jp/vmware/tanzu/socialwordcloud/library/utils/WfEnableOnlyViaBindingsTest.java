package jp.vmware.tanzu.socialwordcloud.library.utils;

import jp.vmware.tanzu.socialwordcloud.library.servicebindings.WfEnableOnlyViaBindings;
import jp.vmware.tanzu.socialwordcloud.library.test_utils.FluentMap;
import org.assertj.core.api.MapAssert;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.bindings.Binding;
import org.springframework.cloud.bindings.Bindings;
import org.springframework.mock.env.MockEnvironment;

import java.nio.file.Paths;
import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;

class WfEnableOnlyViaBindingsTest {

	private final Bindings noWfbindings = new Bindings(new Binding("test-name", Paths.get("test-path"),
			new FluentMap().withEntry(Binding.TYPE, "aaaa")
				.withEntry("api-token", "test-api-token")
				.withEntry("uri", "test-uri")));

	private final Bindings wfbindings = new Bindings(new Binding("test-name", Paths.get("test-path"),
			new FluentMap().withEntry(Binding.TYPE, "wavefront")
				.withEntry("api-token", "test-api-token")
				.withEntry("uri", "test-uri")));

	private final MockEnvironment environment = new MockEnvironment();

	private final HashMap<String, Object> properties = new HashMap<>();

	@Test
	void setNoWfbindings() {
		new WfEnableOnlyViaBindings().process(environment, noWfbindings, properties);
		MapAssert<String, Object> stringObjectMapAssert = assertThat(properties)
			.containsEntry("management.metrics.export.wavefront.enabled", "false")
			.containsEntry("wavefront.tracing.enabled", "false")
			.containsEntry("wavefront.freemium-account", "false");
	}

	@Test
	void setWfbindings() {
		new WfEnableOnlyViaBindings().process(environment, wfbindings, properties);
		MapAssert<String, Object> stringObjectMapAssert = assertThat(properties)
			.containsEntry("management.metrics.export.wavefront.enabled", "true")
			.containsEntry("wavefront.tracing.enabled", "true")
			.containsEntry("wavefront.freemium-account", "false");
	}

}