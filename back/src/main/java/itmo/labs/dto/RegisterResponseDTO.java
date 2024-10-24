package itmo.labs.dto;

import itmo.labs.model.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
public class RegisterResponseDTO {
    private Integer id;
    private String username;
    private Set<Role> roles;
}