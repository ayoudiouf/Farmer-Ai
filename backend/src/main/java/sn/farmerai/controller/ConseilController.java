package sn.farmerai.controller;

import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import sn.farmerai.rag.RagConseilService;

@RestController
@RequestMapping("/api/conseils")
@RequiredArgsConstructor
public class ConseilController {

    private final RagConseilService ragConseilService;

    public record DemandeConseil(@NotBlank String question, String langue) {}

    @PostMapping("/demander")
    public RagConseilService.ReponseConseil demander(@RequestBody DemandeConseil demande) {
        String langue = (demande.langue() == null || demande.langue().isBlank()) ? "français" : demande.langue();
        return ragConseilService.repondre(demande.question(), langue);
    }
}
