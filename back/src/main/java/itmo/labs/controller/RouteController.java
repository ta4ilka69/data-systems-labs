package itmo.labs.controller;

import itmo.labs.dto.RouteDTO;
import itmo.labs.model.Route;
import itmo.labs.service.RouteService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/routes")
public class RouteController {

    @Autowired
    private RouteService routeService;

    /**
     * Create a new Route
     *
     * @param routeDTO the Route data
     * @return the created Route
     */
    @PostMapping
    public ResponseEntity<RouteDTO> createRoute(@Valid @RequestBody RouteDTO routeDTO) {
        Route createdRoute = routeService.createRoute(routeDTO);
        RouteDTO createdRouteDTO = RouteDTO.convertToDTO(createdRoute);
        return new ResponseEntity<>(createdRouteDTO, HttpStatus.CREATED);
    }

    /**
     * Delete all routes with a specific rating
     *
     * @param rating the rating to match
     * @return success message
     */
    @DeleteMapping("/rating/{rating}")
    public ResponseEntity<String> deleteRoutesByRating(@PathVariable int rating) {
        routeService.deleteRoutesByRating(rating);
        return new ResponseEntity<>("Routes with rating " + rating + " deleted successfully.", HttpStatus.OK);
    }

    /**
     * Get all routes with rating less than the specified value
     *
     * @param rating the rating threshold
     * @return list of matching routes
     */
    @GetMapping("/ratingLessThan/{rating}")
    public ResponseEntity<List<RouteDTO>> getRoutesByRatingLessThan(@PathVariable int rating) {
        List<Route> routes = routeService.getRoutesByRatingLessThan(rating);
        List<RouteDTO> routeDTOs = routes.stream()
                .map(RouteDTO::convertToDTO)
                .collect(Collectors.toList());
        return new ResponseEntity<>(routeDTOs, HttpStatus.OK);
    }
        /**
     * Find routes between specified locations with sorting
     *
     * @param fromLocation the name of the origin location
     * @param toLocation   the name of the destination location
     * @param sortBy       the parameter to sort by (e.g., "distance", "rating",
     *                     "name")
     * @return sorted list of matching routes
     */
    @GetMapping("/searchBetweenLocations")
    public ResponseEntity<List<RouteDTO>> findRoutesBetweenLocations(
            @RequestParam String fromLocation,
            @RequestParam String toLocation,
            @RequestParam String sortBy) {
        List<Route> routes = routeService.findRoutesBetweenLocations(fromLocation, toLocation, sortBy);
        List<RouteDTO> routeDTOs = routes.stream()
                .map(RouteDTO::convertToDTO)
                .collect(Collectors.toList());
        return new ResponseEntity<>(routeDTOs, HttpStatus.OK);
    }

    /**
     * Get all Routes
     *
     * @return list of Routes
     */
    @GetMapping
    public ResponseEntity<List<RouteDTO>> getAllRoutes() {
        List<RouteDTO> routes = routeService.getAllRoutes()
                .stream()
                .map(RouteDTO::convertToDTO)
                .collect(Collectors.toList());
        return new ResponseEntity<>(routes, HttpStatus.OK);
    }

    /**
     * Update a Route
     *
     * @param id       the Route ID
     * @param routeDTO the updated Route data
     * @return the updated Route
     */
    @PutMapping("/{id}")
    public ResponseEntity<RouteDTO> updateRoute(@PathVariable Integer id,
            @Valid @RequestBody RouteDTO routeDTO) {
        Route updatedRoute = routeService.updateRoute(id, routeDTO);
        RouteDTO updatedRouteDTO = RouteDTO.convertToDTO(updatedRoute);
        return new ResponseEntity<>(updatedRouteDTO, HttpStatus.OK);
    }

    /**
     * Delete a Route
     *
     * @param id the Route ID
     * @return success message
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteRoute(@PathVariable Integer id) {
        routeService.deleteRoute(id);
        return new ResponseEntity<>("Route deleted successfully.", HttpStatus.OK);
    }
}