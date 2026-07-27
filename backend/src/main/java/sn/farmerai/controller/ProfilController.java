package sn.farmerai.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sn.farmerai.dto.ProfilDtos.*;
import sn.farmerai.model.User;
import sn.farmerai.repository.UserRepository;
import sn.farmerai.service.PhotoStorageService;

import java.io.IOException;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class ProfilController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PhotoStorageService photoStorageService;

    private User utilisateurConnecte() {
        String telephone = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByTelephone(telephone)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
    }

    @GetMapping("/me")
    public ProfilResponse monProfil() {
        User user = utilisateurConnecte();
        return new ProfilResponse(
                user.getTelephone(),
                user.getNomComplet(),
                user.getRegion(),
                user.getLangue() != null ? user.getLangue().name() : null,
                user.getPhotoUrl()
        );
    }

    @PutMapping("/me")
    public ResponseEntity<?> mettreAJourProfil(@RequestBody MettreAJourProfilRequest req) {
        User user = utilisateurConnecte();

        if (req.nomComplet() != null && !req.nomComplet().isBlank()) {
            user.setNomComplet(req.nomComplet());
        }
        if (req.region() != null) {
            user.setRegion(req.region());
        }
        if (req.langue() != null) {
            user.setLangue(User.LangueLocale.valueOf(req.langue()));
        }

        userRepository.save(user);
        return ResponseEntity.ok(monProfil());
    }

    @PutMapping("/me/mot-de-passe")
    public ResponseEntity<?> changerMotDePasse(@RequestBody ChangerMotDePasseRequest req) {
        User user = utilisateurConnecte();

        if (!passwordEncoder.matches(req.ancienMotDePasse(), user.getMotDePasseHash())) {
            return ResponseEntity.badRequest().body("Ancien mot de passe incorrect.");
        }

        user.setMotDePasseHash(passwordEncoder.encode(req.nouveauMotDePasse()));
        userRepository.save(user);
        return ResponseEntity.ok("Mot de passe mis à jour.");
    }

    @PostMapping(value = "/me/photo", consumes = "multipart/form-data")
    public ResponseEntity<?> uploaderPhoto(@RequestParam("photo") MultipartFile photo) throws IOException {
        User user = utilisateurConnecte();
        String cheminRelatif = photoStorageService.sauvegarder(photo);
        user.setPhotoUrl(cheminRelatif);
        userRepository.save(user);
        return ResponseEntity.ok(monProfil());
    }
}