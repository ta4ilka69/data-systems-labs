package itmo.labs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import itmo.labs.model.Route;

@Repository
public interface RouteRepository extends JpaRepository<Route, Integer> {
    boolean existsByNameIgnoreCase(String name);
}
