package ghost.nest.service;

import ghost.nest.client.MlServiceClient;
import ghost.nest.dto.MlResponseDTO;
import ghost.nest.dto.MlRoiRequestDTO;
import ghost.nest.dto.RoiResponseDTO;
import ghost.nest.entity.InfrastructureProject;
import ghost.nest.entity.Property;
import ghost.nest.exception.ResourceNotFoundException;
import ghost.nest.repository.InfrastructureProjectRepository;
import ghost.nest.repository.PropertyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoiService {

    private final PropertyRepository propertyRepository;
    private final InfrastructureProjectRepository infraRepository;
    private final MlServiceClient mlServiceClient;

    @Value("${geospatial.default-radius-meters:1000}")
    private Double defaultRadiusMeters;

    public RoiResponseDTO predictRoi(Long propertyId) {
        log.info("Predicting ROI for property ID: {}", propertyId);
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found"));

        Double lat = property.getLatitude();
        Double lng = property.getLongitude();

        List<InfrastructureProject> infraProjects = infraRepository.findWithinRadius(lat, lng, defaultRadiusMeters);

        MlRoiRequestDTO requestParams = MlRoiRequestDTO.builder()
                .price(property.getPrice().doubleValue())
                .areaSqft(property.getAreaSqft())
                .infrastructureImpact(aggregateInfraImpact(infraProjects))
                .upcomingProjectsCount(infraProjects.size())
                .build();

        MlResponseDTO response = mlServiceClient.predictRoi(requestParams);
        Double roi = response.getPredictedRoi() != null ? response.getPredictedRoi() : 5.0;
        String confidence = response.getConfidence() != null ? response.getConfidence() : "MEDIUM";
        return RoiResponseDTO.builder()
                .propertyId(propertyId)
                .predictedRoiPercentage(roi)
                .confidenceLevel(confidence)
                .build();
    }

    private Double aggregateInfraImpact(List<InfrastructureProject> projects) {
        return projects.stream()
                .mapToDouble(InfrastructureProject::getImpactScore)
                .sum();
    }


}
