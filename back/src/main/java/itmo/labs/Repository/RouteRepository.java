package itmo.labs.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import itmo.labs.Model.Route;

@Repository
public interface RouteRepository extends JpaRepository<Route, Integer> {

}
