package itmo.labs.service;

import itmo.labs.model.Coordinates;
import itmo.labs.model.Location;
import itmo.labs.model.Route;
import itmo.labs.model.User;
import itmo.labs.repository.CoordinatesRepository;
import itmo.labs.repository.LocationRepository;
import itmo.labs.repository.RouteRepository;
import itmo.labs.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class RouteService {
    private final RouteRepository routeRepository;
    private final LocationRepository locationRepository;
    private final CoordinatesRepository coordinatesRepository;
    private final UserRepository userRepository;

    public RouteService(RouteRepository routeRepository,
            LocationRepository locationRepository,
            CoordinatesRepository coordinatesRepository,
            UserRepository userRepository) {
        this.routeRepository = routeRepository;
        this.locationRepository = locationRepository;
        this.coordinatesRepository = coordinatesRepository;
        this.userRepository = userRepository;
    }

    public Route createRoute(Route route) {
        // Retrieve the currently authenticated user
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("User not found: " + currentUsername));

        route.setCreatedBy(currentUser);

        // Handle 'from' Location
        Location from = route.getFrom();
        if (from.getId() != null) {
            Optional<Location> existingFrom = locationRepository.findById(from.getId());
            existingFrom.ifPresent(route::setFrom);
        } else {
            locationRepository.save(from);
        }

        // Handle 'to' Location
        Location to = route.getTo();
        if (to != null) {
            if (to.getId() != null) {
                Optional<Location> existingTo = locationRepository.findById(to.getId());
                existingTo.ifPresent(route::setTo);
            } else {
                locationRepository.save(to);
            }
        }

        // Handle Coordinates
        Coordinates coordinates = route.getCoordinates();
        coordinatesRepository.save(coordinates);

        return routeRepository.save(route);
    }

    public Route getRouteById(Integer id) {
        return routeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Route not found with id: " + id));
    }

    public List<Route> getAllRoutes() {
        return routeRepository.findAll();
    }

    public Route updateRoute(Integer id, Route routeDetails) {
        Route route = getRouteById(id);

        // Retrieve the currently authenticated user
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("User not found: " + currentUsername));

        // Check if current user is the creator or has ADMIN role
        if (!route.getCreatedBy().equals(currentUser) && !currentUser.getRoles().contains(itmo.labs.model.Role.ADMIN)) {
            throw new RuntimeException("You do not have permission to update this route.");
        }

        if (routeDetails.getName() != null && !routeDetails.getName().isEmpty()) {
            route.setName(routeDetails.getName());
        }
        if (routeDetails.getCoordinates() != null) {
            Coordinates coordinates = routeDetails.getCoordinates();
            coordinatesRepository.save(coordinates);
            route.setCoordinates(coordinates);
        }
        if (routeDetails.getFrom() != null) {
            Location from = routeDetails.getFrom();
            if (from.getId() != null) {
                Optional<Location> existingFrom = locationRepository.findById(from.getId());
                existingFrom.ifPresent(route::setFrom);
            } else {
                locationRepository.save(from);
                route.setFrom(from);
            }
        }
        if (routeDetails.getTo() != null) {
            Location to = routeDetails.getTo();
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
        }
        route.setRating(routeDetails.getRating());

        return routeRepository.save(route);
    }

    public void deleteRoute(Integer id) {
        Route route = getRouteById(id);
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("User not found: " + currentUsername));
        if (!route.getCreatedBy().equals(currentUser) && !currentUser.getRoles().contains(itmo.labs.model.Role.ADMIN)) {
            throw new RuntimeException("You do not have permission to delete this route.");
        }
        routeRepository.delete(route);
    }
}