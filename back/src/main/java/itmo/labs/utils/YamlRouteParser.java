package itmo.labs.utils;

import itmo.labs.dto.*;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.List;

public class YamlRouteParser {

    @SuppressWarnings("unchecked")
    public static YamlUploadDTO parseYamlFile(InputStream inputStream) {
        LoaderOptions options = new LoaderOptions();
        Constructor constructor = new Constructor(LinkedHashMap.class, options);
        Yaml yaml = new Yaml(constructor);
        try {
            LinkedHashMap<String, Object> content = yaml.load(inputStream);
            YamlUploadDTO YamlUploadDTO = new YamlUploadDTO();
            int keys = 0;
            // Parse routes if present
            if (content.containsKey("routes")) {
                List<LinkedHashMap<String, Object>> rawRoutes = (List<LinkedHashMap<String, Object>>) content
                        .get("routes");
                YamlUploadDTO.setRoutes(rawRoutes.stream()
                        .map(YamlRouteParser::convertToRouteDTO)
                        .toList());
                keys++;
            }

            // Parse coordinates if present
            if (content.containsKey("coordinates")) {
                List<LinkedHashMap<String, Object>> rawCoordinates = (List<LinkedHashMap<String, Object>>) content
                        .get("coordinates");
                YamlUploadDTO.setCoordinates(rawCoordinates.stream()
                        .map(YamlRouteParser::convertToCoordinatesDTO)
                        .toList());
                keys++;
            }

            // Parse locations if present
            if (content.containsKey("locations")) {
                List<LinkedHashMap<String, Object>> rawLocations = (List<LinkedHashMap<String, Object>>) content
                        .get("locations");
                YamlUploadDTO.setLocations(rawLocations.stream()
                        .map(YamlRouteParser::convertToLocationDTO)
                        .toList());
                keys++;
            }
            if (content.keySet().size() - keys != 0)
                throw new RuntimeException("Error parsing YAML file: incorrect format");
            return YamlUploadDTO;
        } catch (Exception e) {
            throw new RuntimeException("Error parsing YAML file: incorrect format", e);
        }
    }

    private static CoordinatesDTO convertToCoordinatesDTO(LinkedHashMap<String, Object> map) {
        CoordinatesDTO coordinates = new CoordinatesDTO();
        coordinates.setX(((Number) map.get("x")).floatValue());
        coordinates.setY(((Number) map.get("y")).doubleValue());
        return coordinates;
    }

    private static LocationDTO convertToLocationDTO(LinkedHashMap<String, Object> map) {
        LocationDTO location = new LocationDTO();
        location.setName((String) map.get("name"));
        location.setX(((Number) map.get("x")).floatValue());
        location.setY(((Number) map.get("y")).floatValue());
        return location;
    }

    @SuppressWarnings("unchecked")
    private static RouteDTO convertToRouteDTO(LinkedHashMap<String, Object> map) {
        RouteDTO routeDTO = new RouteDTO();
        routeDTO.setName((String) map.get("name"));

        // Convert coordinates
        LinkedHashMap<String, Object> coordMap = (LinkedHashMap<String, Object>) map.get("coordinates");
        CoordinatesDTO coordinates = new CoordinatesDTO();
        coordinates.setX(((Number) coordMap.get("x")).floatValue());
        coordinates.setY(((Number) coordMap.get("y")).doubleValue());
        routeDTO.setCoordinates(coordinates);

        // Convert from location
        LinkedHashMap<String, Object> fromMap = (LinkedHashMap<String, Object>) map.get("from");
        LocationDTO fromLocation = new LocationDTO();
        fromLocation.setName((String) fromMap.get("name"));
        fromLocation.setX(((Number) fromMap.get("x")).floatValue());
        fromLocation.setY(((Number) fromMap.get("y")).floatValue());
        routeDTO.setFrom(fromLocation);

        // Convert to location if present
        LinkedHashMap<String, Object> toMap = (LinkedHashMap<String, Object>) map.get("to");
        if (toMap != null) {
            LocationDTO toLocation = new LocationDTO();
            toLocation.setName((String) toMap.get("name"));
            toLocation.setX(((Number) toMap.get("x")).floatValue());
            toLocation.setY(((Number) toMap.get("y")).floatValue());
            routeDTO.setTo(toLocation);
        }

        routeDTO.setDistance(((Number) map.get("distance")).intValue());
        routeDTO.setRating(((Number) map.get("rating")).intValue());
        routeDTO.setAllowAdminEditing((Boolean) map.get("allowAdminEditing"));

        return routeDTO;
    }
}