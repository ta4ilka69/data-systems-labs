package itmo.labs.controller;

import itmo.labs.dto.CoordinatesDTO;
import itmo.labs.dto.LocationDTO;
import itmo.labs.dto.RouteDTO;
import itmo.labs.model.Coordinates;
import itmo.labs.model.Location;
import itmo.labs.model.Route;
import itmo.labs.model.User;
import itmo.labs.service.RouteService;
import itmo.labs.service.UserService;
import java.time.format.DateTimeFormatter;
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
                .map(this::convertToDTO)
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
        RouteDTO responseDTO = convertToDTO(createdRoute);
        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }

    /**
     * Convert Route entity to RouteDTO
     *
     * @param route the Route entity
     * @return the RouteDTO
     */
    private RouteDTO convertToDTO(Route route) {
        RouteDTO dto = new RouteDTO();
        dto.setId(route.getId());
        dto.setName(route.getName());
        dto.setCoordinates(convertToDTO(route.getCoordinates()));
        dto.setCreationDate(route.getCreationDate().format(DateTimeFormatter.ISO_DATE_TIME));
        dto.setFrom(convertToDTO(route.getFrom()));
        dto.setTo(route.getTo() != null ? convertToDTO(route.getTo()) : null);
        dto.setDistance(route.getDistance());
        dto.setRating(route.getRating());
        dto.setCreatedById(route.getCreatedBy().getId());
        dto.setCreatedByUsername(route.getCreatedBy().getUsername());
        dto.setAllowAdminEditing(route.isAllowAdminEditing());
        return dto;
    }

    /**
     * Convert Coordinates entity to CoordinatesDTO
     *
     * @param coordinates the Coordinates entity
     * @return the CoordinatesDTO
     */
    private CoordinatesDTO convertToDTO(Coordinates coordinates) {
        return new CoordinatesDTO(coordinates.getX(), coordinates.getY());
    }

    /**
     * Convert Location entity to LocationDTO
     *
     * @param location the Location entity
     * @return the LocationDTO
     */
    private LocationDTO convertToDTO(Location location) {
        return new LocationDTO(location.getId(), location.getX(), location.getY(), location.getName());
    }
}