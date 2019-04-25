package common;

import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;

@ApplicationPath("/")
public class MultiPartApp extends ResourceConfig {
	public MultiPartApp() {
		super(MultiPartFeature.class);
	}
}
