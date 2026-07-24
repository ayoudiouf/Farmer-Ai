package sn.farmerai.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import sn.farmerai.rag.RagConseilService;

/**
 * Webhook USSD - Africa's Talking envoie une requête POST à chaque étape
 * de navigation dans le menu (*123#). La réponse doit commencer par :
 *   "CON " pour continuer le menu (l'utilisateur doit répondre)
 *   "END " pour terminer la session (dernier message affiché)
 *
 * Doc Africa's Talking : https://developers.africastalking.com/docs/ussd/overview
 */
@RestController
@RequestMapping("/api/ussd")
@RequiredArgsConstructor
public class UssdController {

    private final RagConseilService ragConseilService;

    @PostMapping(value = "/callback", produces = MediaType.TEXT_PLAIN_VALUE)
    public String callback(
            @RequestParam("sessionId") String sessionId,
            @RequestParam("phoneNumber") String phoneNumber,
            @RequestParam(value = "text", defaultValue = "") String text) {

        String[] etapes = text.isEmpty() ? new String[0] : text.split("\\*");

        // Étape 0 : menu principal
        if (etapes.length == 0) {
            return "CON Bienvenue sur FarmerAI\n"
                 + "1. Conseil sur une culture\n"
                 + "2. Alerte météo\n"
                 + "3. Parler à un conseiller";
        }

        // Étape 1 : choix "Conseil sur une culture" -> demander la culture
        if (etapes.length == 1 && etapes[0].equals("1")) {
            return "CON Quelle culture ? (ex: mil, arachide, tomate)";
        }

        // Étape 2 : culture saisie -> répondre avec le RAG
        if (etapes.length == 2 && etapes[0].equals("1")) {
            String culture = etapes[1];
            var reponse = ragConseilService.repondre("Conseil général pour la culture : " + culture, "français");
            // USSD limité en caractères (~160) : on tronque si besoin
            String texteCourt = reponse.reponse().length() > 300
                    ? reponse.reponse().substring(0, 300) + "..."
                    : reponse.reponse();
            return "END " + texteCourt;
        }

        // Étape 1 : météo (fonctionnalité à brancher sur ANAM)
        if (etapes.length == 1 && etapes[0].equals("2")) {
            return "END Les alertes météo localisées seront bientôt disponibles pour votre région.";
        }

        // Étape 1 : conseiller humain
        if (etapes.length == 1 && etapes[0].equals("3")) {
            return "END Un conseiller vous contactera prochainement au " + phoneNumber + ".";
        }

        return "END Option invalide. Merci de recommencer.";
    }
}
