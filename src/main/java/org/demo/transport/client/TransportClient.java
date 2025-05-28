package org.demo.transport.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.demo.transport.exception.TransportClientException;
import org.demo.transport.model.Transport;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

/**
 * TransportClient is responsible for handling transport-related operations.
 */
@RequiredArgsConstructor
public class TransportClient {

    final String transportUrl;

    final HttpClient httpClient;
    final ObjectMapper objectMapper;

    /**
     * Fetches a list of transports from external service.
     *
     * @param token the authorization token
     * @return a list of {@link Transport} objects
     * @throws TransportClientException if there is an error during the retrieval process
     */
    public List<Transport> fetchTransports(final String token) {
        try {

            final HttpRequest request = HttpRequest.newBuilder(URI.create(transportUrl))
                    .GET()
                    .header("Authorization", "Bearer " + token)
                    .header("User-Agent", "insomnia/10.3.1")
                    .build();

            final HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new TransportClientException(
                        "Failed to get transports information. HTTP status: %s.".formatted(response.statusCode()));
            }

            return objectMapper.readValue(response.body(), new TypeReference<>() {});

        } catch (IOException e) {
            throw new TransportClientException("Error while retrieving transports information.", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new TransportClientException("Thread interrupted during transports fetch.", e);
        }
    }
}
