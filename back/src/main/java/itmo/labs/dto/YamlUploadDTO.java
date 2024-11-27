package itmo.labs.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class YamlUploadDTO {
    private List<RouteDTO> routes;
    private List<CoordinatesDTO> coordinates;
    private List<LocationDTO> locations;
} 