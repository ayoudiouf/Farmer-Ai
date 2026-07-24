package sn.farmerai.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "diagnostics")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Diagnostic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User agriculteur;

    private String cultureConcernee; // ex: mil, arachide, tomate

    @Column(length = 1024)
    private String urlPhoto; // photo envoyée via WhatsApp, stockée (S3/local)

    private String maladieDetectee; // résultat du modèle CNN

    private Double indiceConfiance; // 0.0 - 1.0

    @Column(length = 2000)
    private String recommandation; // conseil généré (RAG agronomique)

    @Builder.Default
    private LocalDateTime dateAnalyse = LocalDateTime.now();
}
