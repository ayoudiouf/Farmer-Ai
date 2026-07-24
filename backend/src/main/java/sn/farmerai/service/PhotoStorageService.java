package sn.farmerai.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Stockage local des photos de diagnostic.
 * Suffisant pour le pilote (500 agriculteurs). Pour la phase de croissance
 * (An 2, 5000+ utilisateurs), remplacer par un stockage objet (S3, ou
 * équivalent africain) - seule cette classe est à changer, l'interface
 * appelante (DiagnosticController) reste identique.
 */
@Service
public class PhotoStorageService {

    @Value("${app.storage.local-path:./storage/photos}")
    private String basePath;

    public String sauvegarder(MultipartFile photo) throws IOException {
        String dossierJour = LocalDate.now().toString(); // ex: 2026-07-22
        Path dossier = Paths.get(basePath, dossierJour);
        Files.createDirectories(dossier);

        String extension = extraireExtension(photo.getOriginalFilename());
        String nomFichier = UUID.randomUUID() + extension;
        Path cible = dossier.resolve(nomFichier);

        Files.copy(photo.getInputStream(), cible, StandardCopyOption.REPLACE_EXISTING);

        // Chemin relatif stocké en base ; servi ensuite via un endpoint statique ou un CDN
        return dossierJour + "/" + nomFichier;
    }

    private String extraireExtension(String nomOriginal) {
        if (nomOriginal == null || !nomOriginal.contains(".")) {
            return ".jpg";
        }
        return nomOriginal.substring(nomOriginal.lastIndexOf('.'));
    }
}
