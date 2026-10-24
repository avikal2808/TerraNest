package ghost.nest.service;

import ghost.nest.client.MlServiceClient;
import ghost.nest.dto.LivabilityResponseDTO;
import ghost.nest.dto.MlLivabilityRequestDTO;
import ghost.nest.dto.MlResponseDTO;
import ghost.nest.entity.*;
import ghost.nest.exception.ResourceNotFoundException;
import ghost.nest.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class LivabilityService {

    private final PropertyRepository propertyRepository;
    private final EnvironmentalDataRepository envRepository;
    private final CrimeDataRepository crimeRepository;
    private final InfrastructureProjectRepository infraRepository;
    private final MlServiceClient mlServiceClient;

    @Value("${geospatial.default-radius-meters:1000}")
    private Double defaultRadiusMeters;

    public LivabilityResponseDTO calculateLivability(Long propertyId) {
        log.info("Calculating livability for property ID: {}", propertyId);
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found"));

        Double lat = property.getLatitude();
        Double lng = property.getLongitude();

        // Fetch nearby data
        java.time.LocalDateTime sixMonthsAgo = java.time.LocalDateTime.now().minusMonths(6);
        List<EnvironmentalData> envData = envRepository.findWithinRadius(lat, lng, defaultRadiusMeters);
        List<CrimeData> crimeData = crimeRepository.findRecentWithinRadius(lat, lng, defaultRadiusMeters, sixMonthsAgo);
        List<InfrastructureProject> infraProjects = infraRepository.findWithinRadius(lat, lng, defaultRadiusMeters);

        // Aggregate features
        MlLivabilityRequestDTO requestParams = aggregateFeatures(envData, crimeData, infraProjects);

        MlResponseDTO mlResponse = mlServiceClient.predictLivability(requestParams);
        Double score = mlResponse.getLivabilityScore() != null ? mlResponse.getLivabilityScore() : 50.0;
        return LivabilityResponseDTO.builder()
                .propertyId(propertyId)
                .overallScore(score)
                .breakdown(mlResponse.getBreakdown() != null ? mlResponse.getBreakdown() : new HashMap<>())
                .explanation(mlResponse.getExplanation() != null ? mlResponse.getExplanation() : "Based on local data")
                .build();
    }

    private MlLivabilityRequestDTO aggregateFeatures(List<EnvironmentalData> envData,
                                                   List<CrimeData> crimeData,
                                                   List<InfrastructureProject> infraProjects) {
        // Air quality: average AQI (invert scale: lower AQI = better)
        double avgAqi = envData.stream()
                .mapToDouble(EnvironmentalData::getAirQualityIndex)
                .average().orElse(50.0);
        double airQualityScore = Math.max(0, 100 - (avgAqi / 5));

        // Water reliability
        double avgWater = envData.stream()
                .mapToDouble(EnvironmentalData::getWaterReliabilityScore)
                .average().orElse(0.7);

        // Crime severity: lower is better
        double crimeScore = crimeData.isEmpty() ? 100.0 :
                100 - (crimeData.stream().mapToInt(CrimeData::getSeverity).average().orElse(0.0) * 10);

        // Infrastructure impact (positive)
        double infraImpact = infraProjects.stream()
                .mapToDouble(InfrastructureProject::getImpactScore)
                .sum();

        return MlLivabilityRequestDTO.builder()
                .airQuality(airQualityScore)
                .waterReliability(avgWater * 100)
                .safety(Math.max(0, crimeScore))
                .infrastructure(Math.min(100, infraImpact * 100))
                .build();
    }


}
