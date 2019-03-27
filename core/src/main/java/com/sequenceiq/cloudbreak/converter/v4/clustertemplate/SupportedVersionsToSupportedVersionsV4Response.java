package com.sequenceiq.cloudbreak.converter.v4.clustertemplate;

import org.springframework.stereotype.Component;

import com.sequenceiq.cloudbreak.api.endpoint.v4.clusterdefinition.responses.SupportedServiceV4Response;
import com.sequenceiq.cloudbreak.api.endpoint.v4.clusterdefinition.responses.SupportedServicesV4Response;
import com.sequenceiq.cloudbreak.api.endpoint.v4.clusterdefinition.responses.SupportedVersionV4Response;
import com.sequenceiq.cloudbreak.api.endpoint.v4.clusterdefinition.responses.SupportedVersionsV4Response;
import com.sequenceiq.cloudbreak.cmtemplate.generator.support.domain.SupportedService;
import com.sequenceiq.cloudbreak.cmtemplate.generator.support.domain.SupportedVersion;
import com.sequenceiq.cloudbreak.cmtemplate.generator.support.domain.SupportedVersions;
import com.sequenceiq.cloudbreak.converter.AbstractConversionServiceAwareConverter;

@Component
public class SupportedVersionsToSupportedVersionsV4Response
        extends AbstractConversionServiceAwareConverter<SupportedVersions, SupportedVersionsV4Response> {

    @Override
    public SupportedVersionsV4Response convert(SupportedVersions source) {
        SupportedVersionsV4Response supportedVersionsV4Response = new SupportedVersionsV4Response();
        for (SupportedVersion supportedVersion : source.getSupportedVersions()) {

            SupportedVersionV4Response supportedVersionV4Response = new SupportedVersionV4Response();
            supportedVersionV4Response.setType(supportedVersion.getType());
            supportedVersionV4Response.setVersion(supportedVersion.getVersion());

            SupportedServicesV4Response supportedServicesV4Response = new SupportedServicesV4Response();
            for (SupportedService service : supportedVersion.getSupportedServices().getServices()) {
                SupportedServiceV4Response supportedServiceV4Response = new SupportedServiceV4Response();
                supportedServiceV4Response.setName(service.getName());
                supportedServiceV4Response.setDisplayName(service.getDisplayName());

                supportedServicesV4Response.getServices().add(supportedServiceV4Response);
            }
            supportedVersionV4Response.setServices(supportedServicesV4Response);

            supportedVersionsV4Response.getSupportedVersions().add(supportedVersionV4Response);
        }
        return supportedVersionsV4Response;
    }
}
