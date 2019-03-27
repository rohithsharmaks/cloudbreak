package com.sequenceiq.cloudbreak.cmtemplate.generator;

import javax.inject.Inject;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;

import com.sequenceiq.cloudbreak.cmtemplate.CmTemplateProcessorFactory;
import com.sequenceiq.cloudbreak.cmtemplate.generator.configuration.ClusterTemplateGeneratorConfigurationResolver;
import com.sequenceiq.cloudbreak.cmtemplate.generator.dependencies.ServiceDependencyMatrixService;
import com.sequenceiq.cloudbreak.cmtemplate.generator.support.SupportedVersionService;
import com.sequenceiq.cloudbreak.cmtemplate.generator.template.GeneratedClusterTemplateService;

@ContextConfiguration
public class CentralTemplateGeneratorContext {

    @Inject
    private SupportedVersionService supportedVersionService;

    @Inject
    private ServiceDependencyMatrixService serviceDependencyMatrixService;

    @Inject
    private GeneratedClusterTemplateService generatedClusterTemplateService;

    public SupportedVersionService supportedVersionService() {
        return supportedVersionService;
    }

    public ServiceDependencyMatrixService serviceDependencyMatrixService() {
        return serviceDependencyMatrixService;
    }

    public GeneratedClusterTemplateService generatedClusterTemplateService() {
        return generatedClusterTemplateService;
    }

    @Configuration
    @ComponentScan({"com.sequenceiq.cloudbreak.cmtemplate.generator"})
    public static class SpringConfig {

        @Bean
        public ClusterTemplateGeneratorConfigurationResolver clusterTemplateGeneratorConfigurationResolver() {
            return new ClusterTemplateGeneratorConfigurationResolver();
        }

        @Bean
        public CmTemplateProcessorFactory cmTemplateProcessorFactory() {
            return new CmTemplateProcessorFactory();
        }
    }

}
