package itmo.labs.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import itmo.labs.Model.Location;
@Repository
public interface LocationRepository extends JpaRepository<Location,Integer>{

}
