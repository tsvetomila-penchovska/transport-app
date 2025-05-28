package org.demo.transport.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.demo.transport.exception.AuthClientException;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * AuthClient is responsible for handing authentication operations.
 */
@RequiredArgsConstructor
public class AuthClient {

    private final String authUrl;
    private final String scope;
    private final String clientId;
    private final String clientSecret;

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    /**
     * Fetches an access token via client credentials flow.
     *
     * @return the access token as a String
     * @throws AuthClientException if there is an error during the authentication process
     */
    public String fetchAccessToken() {
        try {

            final Map<String, String> formParams = Map.of(
                    "grant_type", "client_credentials",
                    "response_type", "token",
                    "scope", scope,
                    "client_id", clientId,
                    "client_secret", clientSecret
            );

            final HttpRequest request = HttpRequest.newBuilder(URI.create(authUrl))
                    .POST(HttpRequest.BodyPublishers.ofString(buildFormUrlEncodedBody(formParams)))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .build();

            final HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new AuthClientException(
                        "Failed to fetch the authorization token. HTTP status: %s.".formatted(response.statusCode()));
            }

            return extractAccessToken(response);

        } catch (IOException e) {
            throw new AuthClientException("Error while retrieving authorization token.", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new AuthClientException("Thread interrupted during authorization token fetch.", e);
        }
    }

    private String extractAccessToken(HttpResponse<String> response) throws JsonProcessingException {
        return objectMapper.readTree(response.body()).optional("access_token")
                .orElseThrow(() -> new AuthClientException("No access token found in response."))
                .asText();
    }

    private static String buildFormUrlEncodedBody(final Map<String, String> params) {
        return params.entrySet().stream()
                .map(entry -> URLEncoder.encode(entry.getKey(), UTF_8) + "=" +
                        URLEncoder.encode(entry.getValue(), UTF_8))
                .collect(Collectors.joining("&"));
    }
}