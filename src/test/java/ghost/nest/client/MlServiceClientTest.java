package ghost.nest.client;

import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import ghost.nest.dto.MlLivabilityRequestDTO;
import ghost.nest.dto.MlResponseDTO;
import ghost.nest.dto.MlRoiRequestDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class MlServiceClientTest {

    @RegisterExtension
    static WireMockExtension wireMockServer = WireMockExtension.newInstance()
            .options(wireMockConfig().dynamicPort())
            .build();

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("external.ml.base-url", wireMockServer::baseUrl);
    }

    @Autowired
    private MlServiceClient mlServiceClient;

    @Test
    void testPredictLivability_Success() {
        // Mock success response
        wireMockServer.stubFor(post(urlEqualTo("/predict/livability"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"livabilityScore\": 85.5, \"explanation\": \"Good neighbourhood\", \"breakdown\": {\"safety\": 90.0}}")));

        MlLivabilityRequestDTO request = MlLivabilityRequestDTO.builder()
                .airQuality(80.0)
                .waterReliability(75.0)
                .safety(85.0)
                .infrastructure(90.0)
                .build();

        MlResponseDTO response = mlServiceClient.predictLivability(request);

        assertNotNull(response);
        assertEquals(85.5, response.getLivabilityScore());
        assertEquals("Good neighbourhood", response.getExplanation());
        assertNotNull(response.getBreakdown());
        assertEquals(90.0, response.getBreakdown().get("safety"));
    }

    @Test
    void testPredictRoi_Success() {
        // Mock success response
        wireMockServer.stubFor(post(urlEqualTo("/predict/roi"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"predictedRoi\": 12.4, \"confidence\": \"HIGH\", \"explanation\": \"High infra growth\"}")));

        MlRoiRequestDTO request = MlRoiRequestDTO.builder()
                .price(15000000.0)
                .areaSqft(1200.0)
                .infrastructureImpact(0.8)
                .upcomingProjectsCount(3)
                .build();

        MlResponseDTO response = mlServiceClient.predictRoi(request);

        assertNotNull(response);
        assertEquals(12.4, response.getPredictedRoi());
        assertEquals("HIGH", response.getConfidence());
        assertEquals("High infra growth", response.getExplanation());
    }

    @Test
    void testPredictLivability_ServerErrorFallback() {
        // Mock 500 server error
        wireMockServer.stubFor(post(urlEqualTo("/predict/livability"))
                .willReturn(aResponse()
                        .withStatus(500)));

        MlLivabilityRequestDTO request = MlLivabilityRequestDTO.builder()
                .airQuality(80.0)
                .waterReliability(75.0)
                .safety(85.0)
                .infrastructure(90.0)
                .build();

        // The fallback should catch the exception and return calculated values
        MlResponseDTO response = mlServiceClient.predictLivability(request);

        assertNotNull(response);
        // Explanation should contain the fallback message
        assertTrue(response.getExplanation().contains("Backend local heuristic calculation"));
        assertNotNull(response.getLivabilityScore());
        // Verify heuristic formula:
        // airQuality * 0.3 + waterReliability * 0.2 + safety * 0.4 + infrastructure * 0.1
        // 80*0.3 + 75*0.2 + 85*0.4 + 90*0.1 = 24 + 15 + 34 + 9 = 82
        assertEquals(82.0, response.getLivabilityScore());
    }

    @Test
    void testPredictRoi_ServerErrorFallback() {
        // Mock 500 server error
        wireMockServer.stubFor(post(urlEqualTo("/predict/roi"))
                .willReturn(aResponse()
                        .withStatus(500)));

        MlRoiRequestDTO request = MlRoiRequestDTO.builder()
                .price(15000000.0)
                .areaSqft(1200.0)
                .infrastructureImpact(0.8)
                .upcomingProjectsCount(3)
                .build();

        MlResponseDTO response = mlServiceClient.predictRoi(request);

        assertNotNull(response);
        assertTrue(response.getExplanation().contains("Backend local heuristic calculation"));
        // Heuristic prediction: 5.0 + 0.8 * 2.0 = 6.6
        assertEquals(6.6, response.getPredictedRoi());
        assertEquals("LOW", response.getConfidence());
    }

    @Test
    void testPredictLivability_TimeoutFallback() {
        // Mock delayed response (greater than read timeout of 3 seconds)
        wireMockServer.stubFor(post(urlEqualTo("/predict/livability"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"livabilityScore\": 85.5}")
                        .withFixedDelay(4000))); // 4 seconds delay

        MlLivabilityRequestDTO request = MlLivabilityRequestDTO.builder()
                .airQuality(80.0)
                .waterReliability(75.0)
                .safety(85.0)
                .infrastructure(90.0)
                .build();

        MlResponseDTO response = mlServiceClient.predictLivability(request);

        assertNotNull(response);
        assertTrue(response.getExplanation().contains("Backend local heuristic calculation"));
        assertEquals(82.0, response.getLivabilityScore());
    }
}
