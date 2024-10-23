package itmo.labs.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CoordinatesDTO {
    @NotNull(message = "X coordinate cannot be null")
    private Float x;

    @NotNull(message = "Y coordinate cannot be null")
    @Max(value = 552, message = "Y coordinate must be less than or equal to 552")
    private Double y;
}