package sn.farmerai.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sn.farmerai.model.Abonnement;

import java.util.Optional;

public interface AbonnementRepository extends JpaRepository<Abonnement, Long> {
    Optional<Abonnement> findByReferencePaiement(String referencePaiement);
}