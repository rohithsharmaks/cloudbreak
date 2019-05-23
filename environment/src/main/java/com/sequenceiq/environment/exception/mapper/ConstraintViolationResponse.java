package com.sequenceiq.environment.exception.mapper;

import java.util.List;

public class ConstraintViolationResponse {

    private final List<String> constraintViolations;

    ConstraintViolationResponse(List<String> constraintViolations) {
        this.constraintViolations = constraintViolations;
    }

    public List<String> getConstraintViolations() {
        return constraintViolations;
    }
}
