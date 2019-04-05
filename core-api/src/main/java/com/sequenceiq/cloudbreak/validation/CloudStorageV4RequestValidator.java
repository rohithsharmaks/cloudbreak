package com.sequenceiq.cloudbreak.validation;

import static java.util.Arrays.stream;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.base.parameter.storage.CloudStorageV4Parameters;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.request.cluster.storage.CloudStorageV4Request;

public class CloudStorageV4RequestValidator implements ConstraintValidator<ValidCloudStorageV4Request, CloudStorageV4Request> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CloudStorageV4RequestValidator.class);

    private boolean valid;

    @Override
    public boolean isValid(CloudStorageV4Request request, ConstraintValidatorContext constraintValidatorContext) {
        valid = true;
        onlyOneParameterFieldFilled(request, constraintValidatorContext);
        storageLocationShouldNotBeEmpty(request, constraintValidatorContext);
        return valid;
    }

    private void storageLocationShouldNotBeEmpty(CloudStorageV4Request request, ConstraintValidatorContext constraintValidatorContext) {
        if (request.getLocations() == null || request.getLocations().isEmpty()) {
            ValidatorUtil.addConstraintViolation(constraintValidatorContext, "You should define at least one storage location!", "status");
            valid = false;
        }
    }

    private void onlyOneParameterFieldFilled(CloudStorageV4Request request, ConstraintValidatorContext constraintValidatorContext) {
        if (getTheAmountOfFilledCloudStorageV4ParametersOnRequest(request) != 1) {
            ValidatorUtil.addConstraintViolation(constraintValidatorContext, "Only one parameter instance should be filled!", "status");
            valid = false;
        }
    }

    private int getTheAmountOfFilledCloudStorageV4ParametersOnRequest(CloudStorageV4Request request) {
        Stream<Field> fields = Stream.concat(stream(request.getClass().getDeclaredFields()), stream(request.getClass().getSuperclass().getDeclaredFields()));
        int notEmptyParamTypeQuantity = 0;
        for (Field field : fields.filter(field -> getInterfacesOfField(field).contains(CloudStorageV4Parameters.class)).collect(Collectors.toList())) {
            try {
                if (FieldUtils.readField(request, field.getName(), true) != null) {
                    notEmptyParamTypeQuantity++;
                }
            } catch (IllegalAccessException e) {
                LOGGER.warn(String.format("Unable to access field! [%s.%s]", field.getDeclaringClass().getSimpleName(), field.getName()), e);
                return Integer.MAX_VALUE;
            }
        }
        return notEmptyParamTypeQuantity;
    }

    private static List<Class<?>> getInterfacesOfField(Field field) {
        return Arrays.asList(field.getType().getInterfaces());
    }

}
