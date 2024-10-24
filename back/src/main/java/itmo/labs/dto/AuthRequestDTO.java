package itmo.labs.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthRequestDTO {
    @JsonProperty("username")
    @NotBlank(message = "Username cannot be null or empty")
    private String username;
    @JsonProperty("password")
    @NotBlank(message = "Password cannot be null or empty")
    private String password;
}