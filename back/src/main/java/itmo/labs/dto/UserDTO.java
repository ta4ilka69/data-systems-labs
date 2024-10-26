package itmo.labs.dto;

import itmo.labs.model.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
public class UserDTO {
    private Integer id;
    private String username;
    private Set<Role> roles;
    private boolean adminRoleRequested;
}
