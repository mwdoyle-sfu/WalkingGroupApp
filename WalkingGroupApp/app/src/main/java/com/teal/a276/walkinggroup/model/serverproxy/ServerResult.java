package com.teal.a276.walkinggroup.model.serverproxy;

/**
 * Callback for server request between ServerManagers and Views
 * If a request has no return type in the body then result can be empty.
 *
 * @param <T> The Object type to pass to result
 */

public interface ServerResult<T> {
    void result(T ans);
}
