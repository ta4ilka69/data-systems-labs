package itmo.labs.controller;

import itmo.labs.service.RouteImportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/routes")
public class RouteImportController {

    private final RouteImportService routeImportService;

    @Autowired
    public RouteImportController(RouteImportService routeImportService) {
        this.routeImportService = routeImportService;
    }

    /**
     * Endpoint for inporting Routes from YAML file
     *
     * @param file YAML-file with routes
     * @return status of operation
     */
    @PostMapping("/import")
    public ResponseEntity<String> importRoutes(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return new ResponseEntity<>("File not selected for import.", HttpStatus.BAD_REQUEST);
        }
        try {
            routeImportService.importRoutes(file);
            return new ResponseEntity<>("Routes successfully imported.", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error importing routes: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}