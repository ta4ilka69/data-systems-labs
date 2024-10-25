package itmo.labs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import itmo.labs.model.RouteAudit;

@Repository
public interface RouteAuditRepository extends JpaRepository<RouteAudit, Long> {

}