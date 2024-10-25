package itmo.labs.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor  
public class LocationDTO {
    private Integer id; // Read-only, generated by the system

    private float x;
    @NotNull(message = "Y coordinate cannot be null")
    private Integer y;

    @NotBlank(message = "Location name cannot be null or empty")
    private String name;
}