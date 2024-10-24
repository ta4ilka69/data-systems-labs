package itmo.labs.controller;

import itmo.labs.dto.CoordinatesDTO;
import itmo.labs.dto.LocationDTO;
import itmo.labs.dto.RouteDTO;
import itmo.labs.dto.RouteUpdateDTO;
import itmo.labs.model.Coordinates;
import itmo.labs.model.Location;
import itmo.labs.model.Route;
import itmo.labs.service.RouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.time.format.DateTimeFormatter;
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
        Route route = convertToEntity(routeDTO);
        Route createdRoute = routeService.createRoute(route);
        RouteDTO responseDTO = convertToDTO(createdRoute);
        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }

    /**
     * Get all Routes
     *
     * @return list of Routes
     */
    @GetMapping
    public ResponseEntity<List<RouteDTO>> getAllRoutes() {
        List<Route> routes = routeService.getAllRoutes();
        List<RouteDTO> routeDTOs = routes.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return new ResponseEntity<>(routeDTOs, HttpStatus.OK);
    }

    /**
     * Get a Route by ID
     *
     * @param id the Route ID
     * @return the Route
     */
    @GetMapping("/{id}")
    public ResponseEntity<RouteDTO> getRouteById(@PathVariable Integer id) {
        Route route = routeService.getRouteById(id);
        RouteDTO routeDTO = convertToDTO(route);
        return new ResponseEntity<>(routeDTO, HttpStatus.OK);
    }

    /**
     * Update a Route
     *
     * @param id          the Route ID
     * @param routeUpdate the Route update data
     * @return the updated Route
     */
    @PutMapping("/{id}")
    public ResponseEntity<RouteDTO> updateRoute(@PathVariable Integer id,
                                                @Valid @RequestBody RouteUpdateDTO routeUpdate) {
        Route updatedRoute = null;
        if ("update".equalsIgnoreCase(routeUpdate.getAction())) {
            Route routeDetails = convertToEntity(routeUpdate.getRouteDTO());
            updatedRoute = routeService.updateRoute(id, routeDetails);
        } else {
            throw new IllegalArgumentException("Invalid action: " + routeUpdate.getAction());
        }
        RouteDTO responseDTO = convertToDTO(updatedRoute);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    /**
     * Delete a Route
     *
     * @param id the Route ID
     * @return no content
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoute(@PathVariable Integer id) {
        routeService.deleteRoute(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
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
        return dto;
    }

    /**
     * Convert RouteDTO to Route entity
     *
     * @param dto the RouteDTO
     * @return the Route entity
     */
    private Route convertToEntity(RouteDTO dto) {
        Route route = new Route();
        route.setName(dto.getName());
        route.setCoordinates(convertToEntity(dto.getCoordinates()));
        route.setFrom(convertToEntity(dto.getFrom()));
        route.setTo(dto.getTo() != null ? convertToEntity(dto.getTo()) : null);
        route.setDistance(dto.getDistance());
        route.setRating(dto.getRating());
        return route;
    }

    /**
     * Convert Coordinates entity to CoordinatesDTO
     *
     * @param coordinates the Coordinates entity
     * @return the CoordinatesDTO
     */
    private CoordinatesDTO convertToDTO(Coordinates coordinates) {
        CoordinatesDTO dto = new CoordinatesDTO();
        dto.setX(coordinates.getX());
        dto.setY(coordinates.getY());
        return dto;
    }

    /**
     * Convert CoordinatesDTO to Coordinates entity
     *
     * @param dto the CoordinatesDTO
     * @return the Coordinates entity
     */
    private Coordinates convertToEntity(CoordinatesDTO dto) {
        Coordinates coordinates = new Coordinates();
        coordinates.setX(dto.getX());
        coordinates.setY(dto.getY());
        return coordinates;
    }

    /**
     * Convert Location entity to LocationDTO
     *
     * @param location the Location entity
     * @return the LocationDTO
     */
    private LocationDTO convertToDTO(Location location) {
        LocationDTO dto = new LocationDTO();
        dto.setName(location.getName());
        dto.setX(location.getX());
        dto.setY(location.getY());
        return dto;
    }

    /**
     * Convert LocationDTO to Location entity
     *
     * @param dto the LocationDTO
     * @return the Location entity
     */
    private Location convertToEntity(LocationDTO dto) {
        Location location = new Location();
        location.setName(dto.getName());
        location.setX(dto.getX());
        location.setY(dto.getY());
        return location;
    }
}