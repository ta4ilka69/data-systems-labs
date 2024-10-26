package itmo.labs.service;

import itmo.labs.dto.UserDTO;
import itmo.labs.model.Role;
import itmo.labs.model.User;
import itmo.labs.repository.UserRepository;

import java.util.List;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Load user by username for Spring Security
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userOptional = userRepository.findByUsername(username);

        if (userOptional.isEmpty()) {
            throw new UsernameNotFoundException("User not found: " + username);
        }

        User user = userOptional.get();
        Set<SimpleGrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                .collect(Collectors.toSet());

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                authorities);
    }

    /**
     * Register a new user
     */
    @Transactional
    public User registerNewUser(String username, String password, boolean isAdmin) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("Username is already taken");
        }
        String encodedPassword = passwordEncoder.encode(password);
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(encodedPassword);
        Set<Role> roles = isAdmin ? Collections.singleton(Role.ADMIN) : Collections.singleton(Role.USER);
        newUser.setRoles(roles);
        return userRepository.save(newUser);
    }

    /**
     * Assign admin role to a user
     */
    @Transactional
    public void assignAdminRole(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
        user.getRoles().add(Role.ADMIN);
        userRepository.save(user);
    }

    /**
     * Approve admin role request
     */
    @Transactional
    public void approveAdminRoleRequest(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
        if (!user.isAdminRoleRequested()) {
            throw new IllegalArgumentException("No admin role request found for user ID: " + userId);
        }
        user.getRoles().add(Role.ADMIN);
        user.setAdminRoleRequested(false);
        userRepository.save(user);
    }

    /**
     * Request admin role
     */
    @Transactional
    public void requestAdminRole() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
        user.setAdminRoleRequested(true);
        userRepository.save(user);
    }

    /**
     * Get user by username
     */
    @Transactional
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
    }

    /**
     * Get all admin role requests
     */
    @Transactional
    public List<User> getAllAdminRoleRequests() {
        return userRepository.findByAdminRoleRequestedTrue();
    }

    @Transactional
    public UserDTO getUsernameInfo() {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + currentUsername));

        return new UserDTO(currentUser.getId(), currentUser.getUsername(), currentUser.getRoles(),
                currentUser.isAdminRoleRequested());
    }
}