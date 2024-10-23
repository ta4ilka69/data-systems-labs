package itmo.labs.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class Coordinates {
    @NotNull(message = "X coordinate cannot be null")
    @Column(nullable = false)
    private Float x;

    @Column(nullable = false)
    @NotNull(message = "Y coordinate cannot be null")
    @Max(value = 552, message = "Y coordinate must be less than or equal to 552")
    private Double y;
}
