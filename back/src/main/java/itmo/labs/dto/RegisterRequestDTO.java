package itmo.labs.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequestDTO {
    @NotBlank(message = "Username cannot be null or empty")
    private String username;
    @NotBlank(message = "Password cannot be null or empty")
    @Size(min = 5, message = "Password must be at least 5 characters long")
    private String password;
}