package sn.farmerai.repository;

import sn.farmerai.model.Diagnostic;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DiagnosticRepository extends JpaRepository<Diagnostic, Long> {
    List<Diagnostic> findByAgriculteur_IdOrderByDateAnalyseDesc(Long userId);
}
