package itmo.labs.model;

import java.time.LocalDateTime;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Route {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    @NotBlank(message = "Route name cannot be null or empty")
    private String name;

    @NotNull(message = "Coordinates cannot be null")
    @Embedded
    private Coordinates coordinates;

    @Column(nullable = false, updatable = false)
    private final LocalDateTime creationDate = LocalDateTime.now();

    @NotNull(message = "Origin location (from) cannot be null")
    @ManyToOne(optional = false, cascade = CascadeType.ALL)
    @JoinColumn(name = "from_location_id", referencedColumnName = "id", nullable = false)
    private Location from;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "to_location_id", referencedColumnName = "id")
    private Location to;

    @Min(value = 2, message = "Distance must be greater than 1")
    private Integer distance;

    @Min(value = 1, message = "Rating must be greater than 0")
    @Column(nullable = false)
    private int rating;
}
