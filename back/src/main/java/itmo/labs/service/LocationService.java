package itmo.labs.service;

import itmo.labs.model.Location;
import itmo.labs.repository.LocationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;
import java.util.List;
import java.util.Optional;

@Service
public class LocationService {

    private final LocationRepository locationRepository;

    public LocationService(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    public Location saveLocation(Location location) {
        if(location.getX() < -180 || location.getX() > 180 || location.getY() < -90 || location.getY() > 90) {
            throw new IllegalArgumentException("Coordinates of location are out of range");
        }
        return locationRepository.save(location);
    }

    public Optional<Location> findById(Integer id) {
        return locationRepository.findById(id);
    }

    public List<Location> findAllLocations() {
        return locationRepository.findAll();
    }
}