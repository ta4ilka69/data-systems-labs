package itmo.labs.controller;

import itmo.labs.dto.RegisterRequestDTO;
import itmo.labs.dto.RegisterResponseDTO;
import itmo.labs.dto.UserDTO;
import itmo.labs.model.User;
import itmo.labs.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class RegisterController {

    private final UserService userService;

    public RegisterController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Endpoint for user registration
     *
     * @param registerRequest the registration request containing username and
     *                        password
     * @return the registered user's details
     */
    @PostMapping("/register")
    public ResponseEntity<RegisterResponseDTO> registerUser(@Valid @RequestBody RegisterRequestDTO registerRequest) {
        
        User user = userService.registerNewUser(registerRequest.getUsername(),
                registerRequest.getPassword(),
                false);

        RegisterResponseDTO response = new RegisterResponseDTO(user.getId(), user.getUsername(), user.getRoles());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Endpoint for requesting admin role
     *
     * @return success message
     */
    @PostMapping("/request-admin-role")
    public ResponseEntity<String> requestAdminRole() {
        userService.requestAdminRole();
        return new ResponseEntity<>("Admin role request submitted successfully.", HttpStatus.OK);
    }

    @GetMapping("/me")
    public ResponseEntity<UserDTO> getUserInfo() {
        return ResponseEntity.ok(userService.getUsernameInfo());
    }
}