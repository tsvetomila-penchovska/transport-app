package org.demo.transport.exception;

import org.demo.transport.client.AuthClient;

/**
 * Exception thrown when there is an error in the {@link AuthClient} operations.
 */
public class AuthClientException extends RuntimeException {

    public AuthClientException(String message) {
        super(message);
    }

    public AuthClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
