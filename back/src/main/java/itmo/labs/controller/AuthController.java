package itmo.labs.controller;

import itmo.labs.dto.AuthRequestDTO;
import itmo.labs.dto.AuthResponseDTO;
import itmo.labs.security.JwtUtil;
import itmo.labs.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private UserService userService;

    /**
     * Endpoint for user login
     *
     * @param authRequest the authentication request containing username and password
     * @return the JWT token if authentication is successful
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> authenticateUser(@Valid @RequestBody AuthRequestDTO authRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authRequest.getUsername(),
                        authRequest.getPassword()
                )
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Set<String> roles = userDetails.getAuthorities().stream()
                .map(auth -> auth.getAuthority().replace("ROLE_", ""))
                .collect(Collectors.toSet());

        String jwt = jwtUtil.generateToken(userDetails.getUsername(), roles);
        return ResponseEntity.ok(new AuthResponseDTO(jwt));
    }
}