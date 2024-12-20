package itmo.labs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import itmo.labs.model.Location;

@Repository
public interface LocationRepository extends JpaRepository<Location, Integer> {

}
