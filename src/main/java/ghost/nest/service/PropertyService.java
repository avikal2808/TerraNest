package ghost.nest.service;

import ghost.nest.dto.PropertyRequestDTO;
import ghost.nest.dto.PropertyResponseDTO;
import ghost.nest.entity.Property;
import ghost.nest.exception.ResourceNotFoundException;
import ghost.nest.repository.PropertyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PropertyService {

    private final PropertyRepository propertyRepository;

    @Value("${geospatial.default-radius-meters:1000}")
    private Double defaultRadiusMeters;

    @Transactional
    public PropertyResponseDTO createProperty(PropertyRequestDTO request) {
        Property property = Property.builder()
                .title(request.getTitle())
                .price(request.getPrice())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .areaSqft(request.getAreaSqft())
                .build();

        Property saved = propertyRepository.save(property);
        return mapToResponse(saved);
    }

    public PropertyResponseDTO getPropertyById(Long id) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found with id: " + id));
        return mapToResponse(property);
    }

    public List<PropertyResponseDTO> searchPropertiesByLocation(Double lat, Double lng, Double radiusMeters) {
        Double radius = radiusMeters != null ? radiusMeters : defaultRadiusMeters;
        List<Property> properties = propertyRepository.findPropertiesWithinRadius(lat, lng, radius);
        return properties.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<PropertyResponseDTO> getAllProperties() {
        return propertyRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public PropertyResponseDTO updateProperty(Long id, PropertyRequestDTO request) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found with id: " + id));

        property.setTitle(request.getTitle());
        property.setPrice(request.getPrice());
        property.setLatitude(request.getLatitude());
        property.setLongitude(request.getLongitude());
        property.setAreaSqft(request.getAreaSqft());

        Property updated = propertyRepository.save(property);
        return mapToResponse(updated);
    }

    @Transactional
    public void deleteProperty(Long id) {
        if (!propertyRepository.existsById(id)) {
            throw new ResourceNotFoundException("Property not found with id: " + id);
        }
        propertyRepository.deleteById(id);
    }

    private PropertyResponseDTO mapToResponse(Property property) {
        return PropertyResponseDTO.builder()
                .id(property.getId())
                .title(property.getTitle())
                .price(property.getPrice())
                .latitude(property.getLatitude())
                .longitude(property.getLongitude())
                .areaSqft(property.getAreaSqft())
                .build();
    }
}
