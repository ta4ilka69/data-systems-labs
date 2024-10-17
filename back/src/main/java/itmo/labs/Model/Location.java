package itmo.labs.Model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "location_id", nullable = false, unique = true)
    private Integer id;

    private float x;

    @NotNull(message = "Y coordinate cannot be null")
    private Integer y;

    @NotBlank(message = "Location name cannot be null or empty")
    @Column(name = "location_name", nullable = false)
    private String name;
}
