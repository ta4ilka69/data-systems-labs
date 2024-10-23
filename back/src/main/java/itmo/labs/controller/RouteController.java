package itmo.labs.controller;

import itmo.labs.model.Route;
import itmo.labs.service.RouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/routes")
public class RouteController {

    @Autowired
    private RouteService routeService;

    @GetMapping
    public List<Route> getAllRoutes() {
        return routeService.findAllRoutes();
    }

    @PostMapping
    public ResponseEntity<Route> createRoute(@RequestBody Route route) {
        Route newRoute = routeService.saveRoute(route);
        return ResponseEntity.ok(newRoute);
    }
}
