package itmo.labs.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import jakarta.validation.Valid;
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

    @NotBlank(message = "Route name cannot be null or empty")
    @Column(nullable = false)
    private String name;

    @NotNull(message = "Coordinates cannot be null")
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "coordinates_id", referencedColumnName = "id")
    @Valid
    private Coordinates coordinates;

    @Column(nullable = false, updatable = false)
    private LocalDateTime creationDate = LocalDateTime.now();

    @NotNull(message = "Origin location cannot be null")
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "from_location_id", referencedColumnName = "location_id", nullable = false)
    @Valid
    private Location from;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "to_location_id", referencedColumnName = "location_id")
    @Valid
    private Location to;

    @Min(value = 2, message = "Distance must be greater than 1")
    @Column
    private Integer distance;

    @Min(value = 1, message = "Rating must be greater than 0")
    @Column(nullable = false)
    private int rating;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id", nullable = false)
    private User createdBy;
}