package jp.vmware.tanzu.socialwordcloud.library.observability;

import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class WfSocialSpans {

	private final Tracer tracer;

	private final String appName;

	private final String socialOrigin;

	public WfSocialSpans(Tracer tracer, @Value("${app.name}") String appName,
						 @Value("${social.origin:Social}") String socialOrigin) {
		this.tracer = tracer;
		this.appName = appName;
		this.socialOrigin = socialOrigin;
	}

	@After("execution(* jp.vmware.tanzu.socialwordcloud.library.utils.SocialMessageHandler.handle(..))")
	public void customizeTwitterSpan() {
		Span span = tracer.currentSpan();
		if (span != null) {
			span.tag("_outboundExternalService", socialOrigin);
			span.tag("_externalApplication", appName);
			span.tag("_externalComponent", "api-endpoint");
		}
	}

}
