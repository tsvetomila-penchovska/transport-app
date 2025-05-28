package org.demo.transport.exception;

import org.demo.transport.client.TransportClient;

/**
 * Exception thrown when there is an error in the {@link TransportClient} operations.
 */
public class TransportClientException extends RuntimeException {

    public TransportClientException(String message) {
        super(message);
    }

    public TransportClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
