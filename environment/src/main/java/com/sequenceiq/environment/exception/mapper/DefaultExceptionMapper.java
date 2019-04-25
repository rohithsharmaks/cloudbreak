package com.sequenceiq.environment.exception.mapper;

import javax.annotation.Nonnull;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.Provider;

import com.sequenceiq.environment.api.model.ExceptionResponse;

@Provider
public class DefaultExceptionMapper extends BaseExceptionMapper<Exception> {

    @Override
    protected Object getEntity(Exception exception) {
        return new ExceptionResponse("Internal server error: " + exception.getMessage());
    }

    @Override
    Status getResponseStatus() {
        return Status.INTERNAL_SERVER_ERROR;
    }

    @Override
    @Nonnull
    Class<Exception> getExceptionType() {
        return Exception.class;
    }

}
