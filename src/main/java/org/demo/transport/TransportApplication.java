package org.demo.transport;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.demo.transport.model.Transport;
import org.demo.transport.client.AuthClient;
import org.demo.transport.client.TransportClient;

import java.net.http.HttpClient;
import java.util.List;

public class TransportApplication {

    public static void main(String[] args) {
        final HttpClient httpClient = HttpClient.newHttpClient();
        final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

        final String authUrl = "https://mbn-provider.authentication.eu12.hana.ondemand.com/oauth/token";
        final String authScope = "interview_demo_transport_app!b923597.transportread";
        final String authClientId = getEnvironmentVariable("CLIENT_ID");
        final String authClientSecret = getEnvironmentVariable("CLIENT_SECRET");
        final String transportUrl = "https://interview-demo-transport-backend.cfapps.eu12.hana.ondemand.com/transports";

        final AuthClient authClient = new AuthClient(authUrl, authScope, authClientId, authClientSecret, httpClient, objectMapper);
        final TransportClient transportClient = new TransportClient(transportUrl, httpClient, objectMapper);

        final String accessToken = authClient.fetchAccessToken();
        final List<Transport> transports = transportClient.fetchTransports(accessToken);
        transports.forEach(System.out::println);
    }

    private static String getEnvironmentVariable(final String key) {
        final String value = System.getenv(key);
        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException("Environment variable %s is not set or is empty.".formatted(key));
        }
        return value;
    }
}