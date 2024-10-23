package itmo.labs.service;

import itmo.labs.model.Route;
import itmo.labs.repository.RouteRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RouteService {

    @Autowired
    private RouteRepository routeRepository;

    public Route saveRoute(Route route) {
        return routeRepository.save(route);
    }

    public List<Route> findAllRoutes() {
        return routeRepository.findAll();
    }
}
