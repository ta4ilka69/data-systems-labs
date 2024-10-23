package itmo.labs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import itmo.labs.model.Coordinates;

@Repository
public interface CoordinatesRepository extends JpaRepository<Coordinates, Integer> {

}
