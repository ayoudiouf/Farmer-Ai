package sn.farmerai.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sn.farmerai.model.Diagnostic;
import sn.farmerai.model.User;
import sn.farmerai.repository.DiagnosticRepository;
import sn.farmerai.repository.UserRepository;
import sn.farmerai.service.CnnDiagnosticClient;
import sn.farmerai.service.PhotoStorageService;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/diagnostics")
@RequiredArgsConstructor
public class DiagnosticController {

    private final DiagnosticRepository diagnosticRepository;
    private final UserRepository userRepository;
    private final CnnDiagnosticClient cnnDiagnosticClient;
    private final PhotoStorageService photoStorageService;

    @PostMapping(value = "/analyser", consumes = "multipart/form-data")
    public ResponseEntity<?> analyserPhoto(
            @RequestParam("photo") MultipartFile photo,
            @RequestParam("culture") String culture,
            @RequestParam("userId") Long userId) {

        User agriculteur = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable : " + userId));

        CnnDiagnosticClient.CnnResult resultat = cnnDiagnosticClient.analyser(photo, culture);

        String cheminPhoto;
        try {
            cheminPhoto = photoStorageService.sauvegarder(photo);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Erreur lors de l'enregistrement de la photo.");
        }

        Diagnostic diagnostic = Diagnostic.builder()
                .agriculteur(agriculteur)
                .cultureConcernee(culture)
                .urlPhoto(cheminPhoto)
                .maladieDetectee(resultat.maladieDetectee())
                .indiceConfiance(resultat.indiceConfiance())
                .recommandation(resultat.recommandation())
                .build();

        return ResponseEntity.ok(diagnosticRepository.save(diagnostic));
    }

    @GetMapping("/utilisateur/{userId}")
    public List<Diagnostic> historiqueUtilisateur(@PathVariable Long userId) {
        return diagnosticRepository.findByAgriculteur_IdOrderByDateAnalyseDesc(userId);
    }
}
