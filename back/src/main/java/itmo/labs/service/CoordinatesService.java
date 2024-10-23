package itmo.labs.service;

import itmo.labs.model.Coordinates;
import itmo.labs.repository.CoordinatesRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CoordinatesService {

    private final CoordinatesRepository coordinatesRepository;

    public CoordinatesService(CoordinatesRepository coordinatesRepository) {
        this.coordinatesRepository = coordinatesRepository;
    }

    public Coordinates saveCoordinates(Coordinates coordinates) {
        return coordinatesRepository.save(coordinates);
    }

    public Optional<Coordinates> findById(Integer id) {
        return coordinatesRepository.findById(id);
    }

    public List<Coordinates> findAllCoordinates() {
        return coordinatesRepository.findAll();
    }
}