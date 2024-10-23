package itmo.labs.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import itmo.labs.Model.Coordinates;

public interface CoordinatesRepository extends JpaRepository<Coordinates,Integer>{

}
