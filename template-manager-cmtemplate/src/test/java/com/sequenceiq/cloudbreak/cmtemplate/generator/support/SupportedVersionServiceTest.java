package com.sequenceiq.cloudbreak.cmtemplate.generator.support;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeansException;
import org.springframework.boot.test.context.SpringBootTestContextBootstrapper;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.BootstrapWith;
import org.springframework.test.context.junit4.SpringRunner;

import com.sequenceiq.cloudbreak.cmtemplate.generator.CentralTemplateGeneratorContext;
import com.sequenceiq.cloudbreak.cmtemplate.generator.support.domain.SupportedVersions;

@RunWith(SpringRunner.class)
@BootstrapWith(SpringBootTestContextBootstrapper.class)
public class SupportedVersionServiceTest extends CentralTemplateGeneratorContext implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Test
    public void testServicesAndDependencies() {
        SupportedVersions supportedVersions = supportedVersionService().collectSupportedVersions();
        Assert.assertTrue(!supportedVersions.getSupportedVersions().isEmpty());
    }
}