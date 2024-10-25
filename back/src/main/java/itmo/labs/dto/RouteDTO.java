package itmo.labs.dto;

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
}