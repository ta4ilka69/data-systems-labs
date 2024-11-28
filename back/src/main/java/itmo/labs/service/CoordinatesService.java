package itmo.labs.service;

import itmo.labs.model.Coordinates;
import itmo.labs.repository.CoordinatesRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(isolation = Isolation.REPEATABLE_READ)
public class CoordinatesService {

    private final CoordinatesRepository coordinatesRepository;

    public CoordinatesService(CoordinatesRepository coordinatesRepository) {
        this.coordinatesRepository = coordinatesRepository;
    }

    public Coordinates saveCoordinates(Coordinates coordinates) {
        if(coordinates.getX() < -180 || coordinates.getX() > 180 || coordinates.getY() < -90 || coordinates.getY() > 90) {
            throw new IllegalArgumentException("Coordinates are out of range");
        }
        return coordinatesRepository.save(coordinates);
    }

    public Optional<Coordinates> findById(Integer id) {
        return coordinatesRepository.findById(id);
    }

    public List<Coordinates> findAllCoordinates() {
        return coordinatesRepository.findAll();
    }
}