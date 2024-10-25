package itmo.labs.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CoordinatesDTO {
    @NotNull(message = "X coordinate cannot be null")
    private Float x;

    @NotNull(message = "Y coordinate cannot be null")
    @DecimalMax(value = "552.0", inclusive = true, message = "Y coordinate must be less than or equal to 552")
    private Double y;
}