package itmo.labs.service;

import itmo.labs.model.Route;
import itmo.labs.repository.RouteRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RouteService {
    private final RouteRepository routeRepository;

    public RouteService(RouteRepository routeRepository) {
        this.routeRepository = routeRepository;
    }

    public Route createRoute(Route route) {
        return routeRepository.save(route);
    }

    public Route getRouteById(Integer id) {
        return routeRepository.findById(id.intValue())
                .orElseThrow(() -> new RuntimeException("Route not found"));
    }

    public List<Route> getAllRoutes() {
        return routeRepository.findAll();
    }

    public Route updateRoute(Integer id, Route routeDetails) {
        Route route = getRouteById(id);
        if (routeDetails.getName() != null) {
            route.setName(routeDetails.getName());
        }
        if (routeDetails.getCoordinates() != null) {
            route.setCoordinates(routeDetails.getCoordinates());
        }
        if (routeDetails.getFrom() != null) {
            route.setFrom(routeDetails.getFrom());
        }
        if (routeDetails.getTo() != null) {
            route.setTo(routeDetails.getTo());
        }
        route.setDistance(routeDetails.getDistance());
        return routeRepository.save(route);
    }

    public void deleteRoute(Integer id) {
        Route route = getRouteById(id);
        routeRepository.delete(route);
    }
}