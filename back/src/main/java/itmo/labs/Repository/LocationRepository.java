package itmo.labs.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import itmo.labs.Model.Location;

public interface LocationRepository extends JpaRepository<Location,Integer>{

}
