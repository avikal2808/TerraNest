package ghost.nest.client;

import ghost.nest.dto.MlLivabilityRequestDTO;
import ghost.nest.dto.MlResponseDTO;
import ghost.nest.dto.MlRoiRequestDTO;
import ghost.nest.exception.ExternalServiceException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class MlServiceClient {

    private final RestClient mlRestClient;

    @Value("${external.ml.endpoints.livability}")
    private String livabilityEndpoint;

    @Value("${external.ml.endpoints.roi}")
    private String roiEndpoint;

    /**
     * Calls ML service to get livability score based on aggregated features.
     */
    @CircuitBreaker(name = "mlService", fallbackMethod = "fallbackPredictLivability")
    public MlResponseDTO predictLivability(MlLivabilityRequestDTO request) {
        log.info("Sending Livability prediction request to ML service");
        try {
            return mlRestClient.post()
                    .uri(livabilityEndpoint)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .body(MlResponseDTO.class);
        } catch (Exception ex) {
            log.error("ML Livability service error: {}", ex.getMessage());
            throw new ExternalServiceException("ML Livability prediction failed", ex);
        }
    }

    public MlResponseDTO fallbackPredictLivability(MlLivabilityRequestDTO request, Throwable throwable) {
        String traceId = MDC.get("traceId");
        log.error("ML Livability circuit breaker fallback triggered. TraceID: {}, Error: {}", traceId, throwable.getMessage());
        
        double score = (request.getAirQuality() != null ? request.getAirQuality() : 50.0) * 0.3 +
                (request.getWaterReliability() != null ? request.getWaterReliability() : 50.0) * 0.2 +
                (request.getSafety() != null ? request.getSafety() : 50.0) * 0.4 +
                (request.getInfrastructure() != null ? request.getInfrastructure() : 50.0) * 0.1;

        Map<String, Double> breakdown = new HashMap<>();
        breakdown.put("airQuality", request.getAirQuality());
        breakdown.put("waterReliability", request.getWaterReliability());
        breakdown.put("safety", request.getSafety());
        breakdown.put("infrastructure", request.getInfrastructure());

        return MlResponseDTO.builder()
                .livabilityScore(Math.min(100, score))
                .breakdown(breakdown)
                .explanation("Backend local heuristic calculation (ML Service down)")
                .build();
    }

    /**
     * Calls ML service to get ROI prediction.
     */
    @CircuitBreaker(name = "mlService", fallbackMethod = "fallbackPredictRoi")
    public MlResponseDTO predictRoi(MlRoiRequestDTO request) {
        log.info("Sending ROI prediction request to ML service");
        try {
            return mlRestClient.post()
                    .uri(roiEndpoint)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .body(MlResponseDTO.class);
        } catch (Exception ex) {
            log.error("ML ROI service error: {}", ex.getMessage());
            throw new ExternalServiceException("ML ROI prediction failed", ex);
        }
    }
    public MlResponseDTO fallbackPredictRoi(MlRoiRequestDTO request, Throwable throwable) {
        String traceId = MDC.get("traceId");
        log.error("ML ROI circuit breaker fallback triggered. TraceID: {}, Error: {}", traceId, throwable.getMessage());
        
        double baseRoi = 5.0;
        double infraBonus = request.getInfrastructureImpact() != null ? request.getInfrastructureImpact() * 2.0 : 0.0;
        double predicted = Math.min(25.0, baseRoi + infraBonus);
        
        return MlResponseDTO.builder()
                .predictedRoi(predicted)
                .confidence("LOW")
                .explanation("Backend local heuristic calculation (ML Service down)")
                .build();
    }
}
