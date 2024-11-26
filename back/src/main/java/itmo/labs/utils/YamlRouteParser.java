package itmo.labs.utils;

import itmo.labs.dto.CoordinatesDTO;
import itmo.labs.dto.LocationDTO;
import itmo.labs.dto.RouteDTO;

import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

public class YamlRouteParser {

    public static List<RouteDTO> parseRoutes(InputStream inputStream) {
        LoaderOptions options = new LoaderOptions();
        Constructor constructor = new Constructor(List.class, options);
        Yaml yaml = new Yaml(constructor);
        try{
        List<LinkedHashMap<String, Object>> rawRoutes = yaml.load(inputStream);
        return rawRoutes.stream()
            .map(YamlRouteParser::convertToRouteDTO)
            .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error parsing YAML file: incorrect format", e);
        }
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