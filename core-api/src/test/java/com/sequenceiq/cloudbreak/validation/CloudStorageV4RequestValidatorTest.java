package com.sequenceiq.cloudbreak.validation;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Set;

import javax.validation.ConstraintValidatorContext;
import javax.validation.ConstraintValidatorContext.ConstraintViolationBuilder;
import javax.validation.ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.base.parameter.storage.AdlsCloudStorageV4Parameters;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.base.parameter.storage.AdlsGen2CloudStorageV4Parameters;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.base.parameter.storage.GcsCloudStorageV4Parameters;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.base.parameter.storage.S3CloudStorageV4Parameters;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.base.parameter.storage.WasbCloudStorageV4Parameters;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.request.cluster.storage.CloudStorageV4Request;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.request.cluster.storage.location.StorageLocationV4Request;

public class CloudStorageV4RequestValidatorTest {

    private CloudStorageV4RequestValidator underTest;

    @Mock
    private ConstraintValidatorContext constraintValidatorContext;

    @Before
    public void setUp() {
        underTest = new CloudStorageV4RequestValidator();
        MockitoAnnotations.initMocks(this);
        ConstraintViolationBuilder constraintViolationBuilder = mock(ConstraintViolationBuilder.class);
        NodeBuilderCustomizableContext nodeBuilderCustomizableContext = mock(NodeBuilderCustomizableContext.class);
        when(constraintValidatorContext.buildConstraintViolationWithTemplate(anyString())).thenReturn(constraintViolationBuilder);
        when(constraintViolationBuilder.addPropertyNode(any())).thenReturn(nodeBuilderCustomizableContext);
    }

    @Test
    public void testWhenOnlyAdlsGen2HasSetThenTheRequestIsValid() {
        CloudStorageV4Request request = new CloudStorageV4Request();
        request.setAdlsGen2(new AdlsGen2CloudStorageV4Parameters());
        setSomeDefaultLocation(request);

        assertTrue(underTest.isValid(request, constraintValidatorContext));
    }

    @Test
    public void testWhenOnlyAdlsHasSetThenTheRequestIsValid() {
        CloudStorageV4Request request = new CloudStorageV4Request();
        request.setAdls(new AdlsCloudStorageV4Parameters());
        setSomeDefaultLocation(request);

        assertTrue(underTest.isValid(request, constraintValidatorContext));
    }

    @Test
    public void testWhenOnlyGcsHasSetThenTheRequestIsValid() {
        CloudStorageV4Request request = new CloudStorageV4Request();
        request.setGcs(new GcsCloudStorageV4Parameters());
        setSomeDefaultLocation(request);

        assertTrue(underTest.isValid(request, constraintValidatorContext));
    }

    @Test
    public void testWhenOnlyS3HasSetThenTheRequestIsValid() {
        CloudStorageV4Request request = new CloudStorageV4Request();
        request.setS3(new S3CloudStorageV4Parameters());
        setSomeDefaultLocation(request);

        assertTrue(underTest.isValid(request, constraintValidatorContext));
    }

    @Test
    public void testWhenOnlyWasbHasSetThenTheRequestIsValid() {
        CloudStorageV4Request request = new CloudStorageV4Request();
        request.setWasb(new WasbCloudStorageV4Parameters());
        setSomeDefaultLocation(request);

        assertTrue(underTest.isValid(request, constraintValidatorContext));
    }

    @Test
    public void testWhenCloudStorageParameterHasSetButLocationsHasNotThenTheRequestIsInvalid() {
        CloudStorageV4Request request = new CloudStorageV4Request();
        request.setAdls(new AdlsCloudStorageV4Parameters());
        request.setLocations(Collections.emptySet());

        assertFalse(underTest.isValid(request, constraintValidatorContext));
    }

    @Test
    public void testWhenCloudStorageParameterHasSetButLocationsAreNullThenTheRequestIsInvalid() {
        CloudStorageV4Request request = new CloudStorageV4Request();
        request.setAdls(new AdlsCloudStorageV4Parameters());
        request.setLocations(null);

        assertFalse(underTest.isValid(request, constraintValidatorContext));
    }

    @Test
    public void testWhenMoreThanOneCloudStorageHasSetThenTheRequestIsInvalid() {
        CloudStorageV4Request request = new CloudStorageV4Request();
        request.setWasb(new WasbCloudStorageV4Parameters());
        request.setS3(new S3CloudStorageV4Parameters());
        setSomeDefaultLocation(request);

        assertFalse(underTest.isValid(request, constraintValidatorContext));
    }

    private static void setSomeDefaultLocation(CloudStorageV4Request request) {
        StorageLocationV4Request locationV4Request = new StorageLocationV4Request();
        request.setLocations(Set.of(locationV4Request));
    }

}