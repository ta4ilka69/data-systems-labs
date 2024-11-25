package itmo.labs.controller;

import itmo.labs.dto.RouteDTO;
import itmo.labs.model.Route;
import itmo.labs.model.User;
import itmo.labs.service.RouteService;
import itmo.labs.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final RouteService routeService;
    private final UserService userService;

    @Autowired
    public AdminController(RouteService routeService, UserService userService) {
        this.routeService = routeService;
        this.userService = userService;
    }

    /**
     * Endpoint to get all admin role requests
     * 
     * @return list of users who requested admin role
     */
    @GetMapping("/admin-role-requests")
    public ResponseEntity<List<User>> getAllAdminRoleRequests() {
        List<User> requests = userService.getAllAdminRoleRequests();
        return new ResponseEntity<>(requests, HttpStatus.OK);
    }

    /**
     * Endpoint to approve an admin role request
     * 
     * @param userId the ID of the user whose request is being approved
     * @return success message
     */
    @PostMapping("/approve-admin-role/{userId}")
    public ResponseEntity<String> approveAdminRoleRequest(@PathVariable Integer userId) {
        userService.approveAdminRoleRequest(userId);
        return new ResponseEntity<>("Admin role approved successfully.", HttpStatus.OK);
    }

    

    /**
     * Get all routes where the name contains the specified substring
     *
     * @param substring the substring to search for in route names
     * @return list of matching routes
     */
    @GetMapping("/routes/searchByName")
    public ResponseEntity<List<RouteDTO>> getRoutesByNameContains(@RequestParam String substring) {
        List<Route> routes = routeService.getRoutesByNameContains(substring);
        List<RouteDTO> routeDTOs = routes.stream()
                .map(RouteDTO::convertToDTO)
                .collect(Collectors.toList());
        return new ResponseEntity<>(routeDTOs, HttpStatus.OK);
    }




    /**
     * Add a new route between specified locations
     *
     * @param routeDTO the Route data
     * @return the created Route
     */
    @PostMapping("/routes/addBetweenLocations")
    public ResponseEntity<RouteDTO> addRouteBetweenLocations(@Valid @RequestBody RouteDTO routeDTO) {
        Route createdRoute = routeService.addRouteBetweenLocations(routeDTO);
        RouteDTO responseDTO = RouteDTO.convertToDTO(createdRoute);
        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }
}