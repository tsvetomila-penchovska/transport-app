package org.demo.transport;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.demo.transport.client.AuthClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthClientTest {

    private static final String AUTH_URL = "http://test/auth/token";
    private static final String SCOPE = "test:scope";
    private static final String CLIENT_ID = "test-client-id";
    private static final String CLIENT_SECRET = "test-client-secret";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private HttpClient mockHttpClient;

    @Mock
    private HttpResponse<String> mockHttpResponse;

    private AuthClient authClient;

    @BeforeEach
    void setUp() {
        authClient = new AuthClient(AUTH_URL, SCOPE, CLIENT_ID, CLIENT_SECRET, mockHttpClient, objectMapper);
    }

    @Test
    @SneakyThrows
    void fetchAccessTokenShouldReturnValidTokenOnSuccess() {
        final HttpRequest expectedRequest = HttpRequest.newBuilder(URI.create(AUTH_URL))
                .POST(HttpRequest.BodyPublishers.ofString(
                        "grant_type=client_credentials&client_id=%s&client_secret=%s&scope=%s"
                                .formatted(CLIENT_ID, CLIENT_SECRET, SCOPE)))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .build();
        final String expectedToken = "valid_token";
        final String mockResponse = "{\"access_token\":\"%s\"}".formatted(expectedToken);

        final ArgumentCaptor<HttpRequest> requestCaptor = ArgumentCaptor.forClass(HttpRequest.class);

        when(mockHttpClient.send(requestCaptor.capture(), eq(HttpResponse.BodyHandlers.ofString())))
                .thenReturn(mockHttpResponse);
        when(mockHttpResponse.statusCode())
                .thenReturn(200);
        when(mockHttpResponse.body())
                .thenReturn(mockResponse);

        final String actualToken = authClient.fetchAccessToken();

        assertNotNull(actualToken);
        assertEquals(expectedToken, actualToken);
        assertEquals(expectedRequest, requestCaptor.getValue());
    }
}
