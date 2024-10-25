package itmo.labs.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CoordinatesDTO {
    @NotNull(message = "X coordinate cannot be null")
    private Float x;

    @NotNull(message = "Y coordinate cannot be null")
    @DecimalMax(value = "552.0", inclusive = true, message = "Y coordinate must be less than or equal to 552")
    private Double y;
}