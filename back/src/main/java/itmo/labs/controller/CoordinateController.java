package itmo.labs.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import itmo.labs.model.Coordinates;
import itmo.labs.service.CoordinatesService;

import java.util.List;

@RestController
@RequestMapping("/api/coordinates")
public class CoordinateController {

    private final CoordinatesService coordinateService;

    @Autowired
    public CoordinateController(CoordinatesService coordinateService) {
        this.coordinateService = coordinateService;
    }

    @GetMapping
    public ResponseEntity<List<Coordinates>> getAllCoordinates() {
        List<Coordinates> coordinates = coordinateService.findAllCoordinates();
        return new ResponseEntity<>(coordinates, HttpStatus.OK);
    }
}