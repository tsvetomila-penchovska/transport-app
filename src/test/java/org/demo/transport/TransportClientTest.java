package org.demo.transport;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.SneakyThrows;
import org.demo.transport.client.TransportClient;
import org.demo.transport.model.Transport;
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
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransportClientTest {

    private static final String TRANSPORT_URL = "http://test/transport";
    private static final String TOKEN = "test-token";
    private static final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Mock
    private HttpClient mockHttpClient;

    @Mock
    private HttpResponse<String> mockHttpResponse;

    private TransportClient transportClient;

    @BeforeEach
    void setUp() {
        transportClient = new TransportClient(TRANSPORT_URL, mockHttpClient, objectMapper);
    }

    @Test
    @SneakyThrows
    void fetchTransportsReturnsListOfTransportOnSuccess() {
        final HttpRequest expectedRequest = HttpRequest.newBuilder(URI.create(TRANSPORT_URL))
                .GET()
                .header("Authorization", "Bearer " + TOKEN)
                .header("User-Agent", "insomnia/10.3.1")
                .build();
        final Transport expectedTransport = new Transport(
                1L,
                "Test Description",
                Transport.Type.SHIP,
                LocalDateTime.parse("2025-01-01T01:01:01.111111"),
                LocalDateTime.parse("2026-01-01T01:01:01.111111"));
        final String mockResponse = """
                [
                    {
                        "id": 1,
                        "description": "Test Description",
                        "type": "Ship",
                        "starttimestamp": "2025-01-01T01:01:01.111111",
                        "endtimestamp": "2026-01-01T01:01:01.111111",
                        "transporterid": null
                    }
                ]
                """;

        final ArgumentCaptor<HttpRequest> requestCaptor = ArgumentCaptor.forClass(HttpRequest.class);

        when(mockHttpClient.send(requestCaptor.capture(), eq(HttpResponse.BodyHandlers.ofString())))
                .thenReturn(mockHttpResponse);
        when(mockHttpResponse.statusCode())
                .thenReturn(200);
        when(mockHttpResponse.body())
                .thenReturn(mockResponse);

        final List<Transport> transports = transportClient.fetchTransports(TOKEN);

        assertNotNull(transports);
        assertEquals(1, transports.size());
        assertEquals(expectedTransport, transports.getFirst());
        assertEquals(expectedRequest, requestCaptor.getValue());
    }
}
