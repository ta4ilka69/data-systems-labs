package itmo.labs.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import itmo.labs.Model.Coordinates;
@Repository
public interface CoordinatesRepository extends JpaRepository<Coordinates,Integer>{

}
