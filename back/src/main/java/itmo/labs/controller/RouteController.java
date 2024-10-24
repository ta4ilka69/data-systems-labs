package itmo.labs.controller;

import itmo.labs.dto.CoordinatesDTO;
import itmo.labs.dto.LocationDTO;
import itmo.labs.dto.RouteDTO;
import itmo.labs.dto.RouteUpdateDTO;
import itmo.labs.model.Coordinates;
import itmo.labs.model.Location;
import itmo.labs.model.Route;
import itmo.labs.service.RouteService;
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

    private final RouteService routeService;
    private final RouteWebSocketController webSocketController;

    public RouteController(RouteService routeService, RouteWebSocketController webSocketController) {
        this.routeService = routeService;
        this.webSocketController = webSocketController;
    }

    @PostMapping
    public ResponseEntity<RouteDTO> createRoute(@Valid @RequestBody RouteDTO routeDTO) {
        Route route = convertToEntity(routeDTO);
        Route createdRoute = routeService.createRoute(route);
        RouteDTO createdRouteDTO = convertToDTO(createdRoute);
        webSocketController.notifyRouteChange(new RouteUpdateDTO("ADD", createdRouteDTO.getId()));
        return new ResponseEntity<>(createdRouteDTO, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RouteDTO> getRoute(@PathVariable Integer id) {
        Route route = routeService.getRouteById(id);
        RouteDTO routeDTO = convertToDTO(route);
        return ResponseEntity.ok(routeDTO);
    }

    @GetMapping
    public ResponseEntity<List<RouteDTO>> getAllRoutes() {
        List<Route> routes = routeService.getAllRoutes();
        List<RouteDTO> routeDTOs = routes.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(routeDTOs);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RouteDTO> updateRoute(@PathVariable Integer id, @Valid @RequestBody RouteDTO routeDTO) {
        Route route = convertToEntity(routeDTO);
        Route updatedRoute = routeService.updateRoute(id, route);
        RouteDTO updatedRouteDTO = convertToDTO(updatedRoute);
        webSocketController.notifyRouteChange(new RouteUpdateDTO("UPDATE", updatedRouteDTO.getId()));
        return ResponseEntity.ok(updatedRouteDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoute(@PathVariable Integer id) {
        routeService.deleteRoute(id);
        webSocketController.notifyRouteChange(new RouteUpdateDTO("DELETE", id));
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
        String message = ex.getMessage();
        HttpStatus status = HttpStatus.BAD_REQUEST;

        if (message.contains("not found")) {
            status = HttpStatus.NOT_FOUND;
        } else if (message.contains("permission")) {
            status = HttpStatus.FORBIDDEN;
        }

        return new ResponseEntity<>(message, status);
    }

    private RouteDTO convertToDTO(Route route) {
        RouteDTO dto = new RouteDTO();
        dto.setId(route.getId());
        dto.setName(route.getName());
        dto.setCoordinates(convertToDTO(route.getCoordinates()));
        dto.setCreationDate(route.getCreationDate().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        dto.setFrom(convertToDTO(route.getFrom()));
        if (route.getTo() != null) {
            dto.setTo(convertToDTO(route.getTo()));
        }
        dto.setDistance(route.getDistance());
        dto.setRating(route.getRating());
        return dto;
    }

    private Route convertToEntity(RouteDTO dto) {
        Route route = new Route();
        route.setName(dto.getName());
        route.setCoordinates(convertToEntity(dto.getCoordinates()));
        route.setFrom(convertToEntity(dto.getFrom()));
        if (dto.getTo() != null) {
            route.setTo(convertToEntity(dto.getTo()));
        }
        route.setDistance(dto.getDistance());
        route.setRating(dto.getRating());
        return route;
    }

    private CoordinatesDTO convertToDTO(Coordinates coordinates) {
        CoordinatesDTO dto = new CoordinatesDTO();
        dto.setX(coordinates.getX());
        dto.setY(coordinates.getY());
        return dto;
    }

    private Coordinates convertToEntity(CoordinatesDTO dto) {
        Coordinates coordinates = new Coordinates();
        coordinates.setX(dto.getX());
        coordinates.setY(dto.getY());
        return coordinates;
    }

    private LocationDTO convertToDTO(Location location) {
        LocationDTO dto = new LocationDTO();
        dto.setId(location.getId());
        dto.setX(location.getX());
        dto.setY(location.getY());
        dto.setName(location.getName());
        return dto;
    }

    private Location convertToEntity(LocationDTO dto) {
        Location location = new Location();
        if (dto.getId() != null) {
            location.setId(dto.getId());
        }
        location.setX(dto.getX());
        location.setY(dto.getY());
        location.setName(dto.getName());
        return location;
    }
}