package itmo.labs.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Coordinates {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull(message = "X coordinate cannot be null")
    @Column(nullable = false)
    private Float x;

    @NotNull(message = "Y coordinate cannot be null")
    @Max(value = 552, message = "Y coordinate must be less than or equal to 552")
    @Column(nullable = false)
    private Double y;
}