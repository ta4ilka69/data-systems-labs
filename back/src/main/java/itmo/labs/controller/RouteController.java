package itmo.labs.controller;

import itmo.labs.dto.RouteUpdateDTO;
import itmo.labs.model.Route;
import itmo.labs.service.RouteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<Route> createRoute(@RequestBody Route route) {
        Route createdRoute = routeService.createRoute(route);

        webSocketController.notifyRouteChange(new RouteUpdateDTO("ADD", createdRoute.getId()));

        return ResponseEntity.ok(createdRoute);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Route> getRoute(@PathVariable Integer id) {
        Route route = routeService.getRouteById(id);
        return ResponseEntity.ok(route);
    }

    @GetMapping
    public ResponseEntity<List<Route>> getAllRoutes() {
        List<Route> routes = routeService.getAllRoutes();
        return ResponseEntity.ok(routes);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Route> updateRoute(@PathVariable Integer id, @RequestBody Route route) {
        Route updatedRoute = routeService.updateRoute(id, route);

        webSocketController.notifyRouteChange(new RouteUpdateDTO("UPDATE", updatedRoute.getId()));

        return ResponseEntity.ok(updatedRoute);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRoute(@PathVariable Integer id) {
        routeService.deleteRoute(id);

        webSocketController.notifyRouteChange(new RouteUpdateDTO("DELETE", id));

        return ResponseEntity.ok().build();
    }
}