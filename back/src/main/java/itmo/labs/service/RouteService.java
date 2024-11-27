package itmo.labs.service;

import itmo.labs.controller.RouteWebSocketController;
import itmo.labs.dto.RouteDTO;
import itmo.labs.dto.RouteUpdateDTO;
import itmo.labs.model.*;
import itmo.labs.repository.CoordinatesRepository;
import itmo.labs.repository.LocationRepository;
import itmo.labs.repository.RouteAuditRepository;
import itmo.labs.repository.RouteRepository;
import itmo.labs.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class RouteService {
    private final RouteRepository routeRepository;
    private final LocationRepository locationRepository;
    private final CoordinatesRepository coordinatesRepository;
    private final UserRepository userRepository;
    private final RouteAuditRepository routeAuditRepository;
    private final RouteWebSocketController routeWebSocketController;

    @Autowired
    public RouteService(RouteRepository routeRepository,
            LocationRepository locationRepository,
            CoordinatesRepository coordinatesRepository,
            UserRepository userRepository,
            RouteAuditRepository routeAuditRepository, RouteWebSocketController routeWebSocketController) {
        this.routeRepository = routeRepository;
        this.locationRepository = locationRepository;
        this.coordinatesRepository = coordinatesRepository;
        this.userRepository = userRepository;
        this.routeAuditRepository = routeAuditRepository;
        this.routeWebSocketController = routeWebSocketController;
    }

    /**
     * Create a new Route
     *
     * @param route the Route entity
     * @return the created Route
     */
    public Route createRoute(RouteDTO routeDTO) {
        // new unique name check
        if (routeRepository.findAll().stream()
                .anyMatch(r -> r.getName().equalsIgnoreCase(routeDTO.getName()))) {
            throw new IllegalArgumentException("Route with name '" + routeDTO.getName() + "' already exists");
        }
        // Retrieve the currently authenticated user
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + currentUsername));

        Route route = RouteDTO.convertToEntity(routeDTO);
        route.setCreatedBy(currentUser);
        route.setCreationDate(LocalDateTime.now());

        Route createdRoute = routeRepository.save(route);

        // Create audit log
        RouteAudit audit = new RouteAudit();
        audit.setRoute(createdRoute);
        audit.setOperationType(OperationType.CREATE);
        audit.setTimestamp(LocalDateTime.now());
        audit.setPerformedBy(currentUser);
        audit.setDescription("Route created with ID: " + createdRoute.getId());
        routeAuditRepository.save(audit);
        routeWebSocketController
                .notifyRouteChange(new RouteUpdateDTO(OperationType.CREATE, createdRoute.getId(), routeDTO));
        return createdRoute;
    }

    /**
     * Update an existing Route
     *
     * @param id           the Route ID
     * @param routeDetails the Route data to update
     * @return the updated Route
     */
    public Route updateRoute(Integer id, RouteDTO routeDetails) {
        Route route = getRouteById(id);
        // new unique name check
        if (routeRepository.findAll().stream()
                .anyMatch(r -> r.getName().equalsIgnoreCase(routeDetails.getName()))) {
            throw new IllegalArgumentException("Route with name '" + routeDetails.getName() + "' already exists");
        }
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + currentUsername));

        // Check permissions
        boolean isOwner = route.getCreatedBy().getId() == currentUser.getId();
        boolean isAdmin = currentUser.getRoles().contains(Role.ADMIN);

        if (!isOwner && !isAdmin) {
            throw new IllegalArgumentException("You do not have permission to update this route.");
        }

        if (isAdmin && !route.isAllowAdminEditing()) {
            throw new IllegalArgumentException("Admin is not allowed to modify this route.");
        }

        // Update fields if provided
        if (routeDetails.getName() != null && !routeDetails.getName().isEmpty()) {
            route.setName(routeDetails.getName());
        }
        if (routeDetails.getCoordinates() != null) {
            Coordinates coordinates = RouteDTO.convertToEntity(routeDetails.getCoordinates());

            // Ensure coordinates have valid data
            if (coordinates.getX() == null || coordinates.getY() == null) {
                throw new IllegalArgumentException("Coordinates X and Y cannot be null.");
            }

            coordinatesRepository.save(coordinates);
            route.setCoordinates(coordinates);
        }
        if (routeDetails.getFrom() != null) {
            Location from = RouteDTO.convertToEntity(routeDetails.getFrom());
            if (from.getId() != null) {
                Optional<Location> existingFrom = locationRepository.findById(from.getId());
                existingFrom.ifPresent(route::setFrom);
            } else {
                locationRepository.save(from);
                route.setFrom(from);
            }
        }
        if (routeDetails.getTo() != null) {
            Location to = RouteDTO.convertToEntity(routeDetails.getTo());
            if (to.getId() != null) {
                Optional<Location> existingTo = locationRepository.findById(to.getId());
                existingTo.ifPresent(route::setTo);
            } else {
                locationRepository.save(to);
                route.setTo(to);
            }
        }
        if (routeDetails.getDistance() != null && routeDetails.getDistance() > 1) {
            route.setDistance(routeDetails.getDistance());
        } else if (routeDetails.getDistance() != null) {
            throw new IllegalArgumentException("Distance must be greater than 1.");
        }
        if (routeDetails.getRating() > 0) {
            route.setRating(routeDetails.getRating());
        } else {
            throw new IllegalArgumentException("Rating must be greater than 0.");
        }

        // Update the allowAdminEditing flag if specified
        route.setAllowAdminEditing(routeDetails.isAllowAdminEditing());
        Route updatedRoute = routeRepository.save(route);
        RouteDTO updatedRouteDTO = RouteDTO.convertToDTO(updatedRoute);
        routeWebSocketController
                .notifyRouteChange(new RouteUpdateDTO(OperationType.UPDATE, updatedRoute.getId(), updatedRouteDTO));
        // Create audit log
        RouteAudit audit = new RouteAudit();
        audit.setRoute(updatedRoute);
        audit.setOperationType(OperationType.UPDATE);
        audit.setTimestamp(LocalDateTime.now());
        audit.setPerformedBy(currentUser);
        audit.setDescription("Route updated with ID: " + updatedRoute.getId());
        routeAuditRepository.save(audit);

        return updatedRoute;
    }

    /**
     * Delete a Route
     *
     * @param id the Route ID
     */
    public void deleteRoute(Integer id) {
        Route route = getRouteById(id);
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + currentUsername));

        boolean isOwner = route.getCreatedBy().getId() == currentUser.getId();
        boolean isAdmin = currentUser.getRoles().contains(Role.ADMIN);

        if (!isOwner && !isAdmin) {
            throw new IllegalArgumentException("You do not have permission to delete this route.");
        }

        if (isAdmin && !route.isAllowAdminEditing()) {
            throw new IllegalArgumentException("Admin is not allowed to delete this route.");
        }

        routeRepository.delete(route);
        routeWebSocketController.notifyRouteChange(new RouteUpdateDTO(OperationType.DELETE, route.getId(), null));
        // Create audit log
        RouteAudit audit = new RouteAudit();
        audit.setRoute(route);
        audit.setOperationType(OperationType.DELETE);
        audit.setTimestamp(LocalDateTime.now());
        audit.setPerformedBy(currentUser);
        audit.setDescription("Route deleted with ID: " + route.getId());
        routeAuditRepository.save(audit);
    }

    /**
     * Retrieve a Route by ID
     *
     * @param id the Route ID
     * @return the Route
     */
    public Route getRouteById(Integer id) {
        return routeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Route not found with ID: " + id));
    }

    /**
     * Get all Routes
     *
     * @return list of Routes
     */
    public List<Route> getAllRoutes() {
        return routeRepository.findAll();
    }

    /**
     * Delete all routes with a specific rating
     *
     * @param rating the rating to match
     */
    public void deleteRoutesByRating(int rating) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + currentUsername));
        List<Route> routesToDelete = routeRepository.findAll()
                .stream()
                .filter(route -> route.getRating() == rating)
                .filter(route -> route.getCreatedBy().getId() == currentUser.getId())
                .collect(Collectors.toList());
        routeRepository.deleteAll(routesToDelete);
        routeWebSocketController.notifyRouteChange(new RouteUpdateDTO(OperationType.DELETE, 0, null));

        for (Route route : routesToDelete) {
            RouteAudit audit = new RouteAudit();
            audit.setRoute(route);
            audit.setOperationType(OperationType.DELETE);
            audit.setTimestamp(LocalDateTime.now());
            audit.setPerformedBy(currentUser);
            audit.setDescription("Route deleted with ID: " + route.getId());
            routeAuditRepository.save(audit);
        }
    }

    /**
     * Get all routes where the name contains the specified substring
     *
     * @param substring the substring to search for in route names
     * @return list of matching routes
     */
    public List<Route> getRoutesByNameContains(String substring) {
        return routeRepository.findAll()
                .stream()
                .filter(route -> route.getName().toLowerCase().contains(substring.toLowerCase()))
                .collect(Collectors.toList());
    }

    /**
     * Get all routes with rating less than the specified value
     *
     * @param rating the rating threshold
     * @return list of matching routes
     */
    public List<Route> getRoutesByRatingLessThan(int rating) {
        return routeRepository.findAll()
                .stream()
                .filter(route -> route.getRating() < rating)
                .collect(Collectors.toList());
    }

    /**
     * Find all routes between specified locations and sort by a given parameter
     *
     * @param fromLocationName the name of the origin location
     * @param toLocationName   the name of the destination location
     * @param sortBy           the parameter to sort by (e.g., "distance", "rating",
     *                         "name")
     * @return sorted list of matching routes
     */
    public List<Route> findRoutesBetweenLocations(String fromLocationName, String toLocationName, String sortBy) {
        List<Route> matchingRoutes = routeRepository.findAll()
                .stream()
                .filter(route -> route.getFrom().getName().equalsIgnoreCase(fromLocationName)
                        && route.getTo() != null
                        && route.getTo().getName().equalsIgnoreCase(toLocationName))
                .collect(Collectors.toList());

        Comparator<Route> comparator;

        switch (sortBy.toLowerCase()) {
            case "distance":
                comparator = Comparator.comparing(Route::getDistance);
                break;
            case "rating":
                comparator = Comparator.comparing(Route::getRating);
                break;
            case "name":
                comparator = Comparator.comparing(Route::getName);
                break;
            default:
                throw new IllegalArgumentException("Invalid sort parameter: " + sortBy);
        }

        return matchingRoutes.stream()
                .sorted(comparator)
                .collect(Collectors.toList());
    }

    /**
     * Add a new route between specified locations
     *
     * @param routeDTO the Route data
     * @return the created Route
     */
    public Route addRouteBetweenLocations(RouteDTO routeDTO) {
        return createRoute(routeDTO);
    }
}