package com.api.config;

import com.api.resource.JournalResource;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.stereotype.Component;

import javax.ws.rs.ApplicationPath;

@Component
@ApplicationPath("/v1")
public class JerseyConfig extends ResourceConfig {

    public JerseyConfig() {
        register(JournalResource.class);
        register(JacksonFeature.class);
    }

}
