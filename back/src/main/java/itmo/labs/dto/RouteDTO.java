package itmo.labs.dto;

import itmo.labs.model.Coordinates;
import itmo.labs.model.Location;
import itmo.labs.model.Route;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RouteDTO {
    private Integer id;

    @NotBlank(message = "Route name cannot be null or empty")
    private String name;

    @NotNull(message = "Coordinates cannot be null")
    private CoordinatesDTO coordinates;

    private String creationDate; // ISO 8601 format

    @NotNull(message = "Origin location cannot be null")
    @Valid
    private LocationDTO from;
    @Valid
    private LocationDTO to;

    @Min(value = 2, message = "Distance must be greater than 1")
    private Integer distance;

    @Min(value = 1, message = "Rating must be greater than 0")
    private int rating;
    private Integer createdById;
    private String createdByUsername;

    private boolean allowAdminEditing;

    /**
     * Convert RouteDTO to Route entity
     *
     * @param dto the RouteDTO
     * @return the Route entity
     */
    public static Route convertToEntity(RouteDTO dto) {
        Route route = new Route();
        route.setName(dto.getName());
        route.setCoordinates(convertToEntity(dto.getCoordinates()));
        route.setFrom(convertToEntity(dto.getFrom()));
        route.setTo(dto.getTo() != null ? convertToEntity(dto.getTo()) : null);
        route.setDistance(dto.getDistance());
        route.setRating(dto.getRating());
        route.setAllowAdminEditing(dto.isAllowAdminEditing());
        return route;
    }

    /**
     * Convert CoordinatesDTO to Coordinates entity
     *
     * @param dto the CoordinatesDTO
     * @return the Coordinates entity
     */
    public static Coordinates convertToEntity(CoordinatesDTO dto) {
        Coordinates coordinates = new Coordinates();
        coordinates.setX(dto.getX());
        coordinates.setY(dto.getY());
        return coordinates;
    }

    /**
     * Convert LocationDTO to Location entity
     *
     * @param dto the LocationDTO
     * @return the Location entity
     */
    public static Location convertToEntity(LocationDTO dto) {
        Location location = new Location();
        location.setName(dto.getName());
        location.setX(dto.getX());
        location.setY(dto.getY());
        return location;
    }

    /**
     * Convert Route entity to RouteDTO
     *
     * @param route the Route entity
     * @return the RouteDTO
     */
    public static RouteDTO convertToDTO(Route route) {
        RouteDTO dto = new RouteDTO();
        dto.setId(route.getId());
        dto.setCreatedById(route.getCreatedBy().getId());
        dto.setCreatedByUsername(route.getCreatedBy().getUsername());
        dto.setName(route.getName());
        dto.setCoordinates(convertToDTO(route.getCoordinates()));
        dto.setFrom(convertToDTO(route.getFrom()));
        dto.setTo(convertToDTO(route.getTo()));
        dto.setDistance(route.getDistance());
        dto.setRating(route.getRating());
        dto.setAllowAdminEditing(route.isAllowAdminEditing());
        return dto;
    }

    /**
     * Convert Coordinates entity to CoordinatesDTO
     *
     * @param coordinates the Coordinates entity
     * @return the CoordinatesDTO
     */
    public static CoordinatesDTO convertToDTO(Coordinates coordinates) {
        return new CoordinatesDTO(coordinates.getX(), coordinates.getY());
    }

    /**
     * Convert Location entity to LocationDTO
     *
     * @param location the Location entity
     * 
     * @return the LocationDTO
     */
    public static LocationDTO convertToDTO(Location location) {
        return new LocationDTO(location.getId(), location.getX(), location.getY(), location.getName());
    }
}