package com.sequenceiq.cloudbreak.converter.v4.clustertemplate;

import org.springframework.stereotype.Component;

import com.sequenceiq.cloudbreak.api.endpoint.v4.clusterdefinition.responses.GeneratedClusterTemplateV4Response;
import com.sequenceiq.cloudbreak.cmtemplate.generator.template.domain.GeneratedClusterTemplate;
import com.sequenceiq.cloudbreak.converter.AbstractConversionServiceAwareConverter;

@Component
public class GeneratedClusterTemplateToGeneratedClusterTemplateV4Response
        extends AbstractConversionServiceAwareConverter<GeneratedClusterTemplate, GeneratedClusterTemplateV4Response> {

    @Override
    public GeneratedClusterTemplateV4Response convert(GeneratedClusterTemplate source) {
        GeneratedClusterTemplateV4Response generatedClusterTemplateV4Response = new GeneratedClusterTemplateV4Response();
        generatedClusterTemplateV4Response.setTemplate(source.getTemplate());
        return generatedClusterTemplateV4Response;
    }
}
