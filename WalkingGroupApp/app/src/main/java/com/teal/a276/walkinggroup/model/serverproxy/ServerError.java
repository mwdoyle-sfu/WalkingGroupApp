package com.teal.a276.walkinggroup.model.serverproxy;


/**
 * Error callback for server request between ServerManagers and Views
 */

public interface ServerError {
    void error(String error);
}
