package sn.farmerai.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/voix")
public class VoiceController {

    @Value("${africastalking.username}")
    private String username;

    @Value("${africastalking.api-key}")
    private String apiKey;

    @Value("${africastalking.caller-id}")
    private String callerId;

    private final RestTemplate restTemplate = new RestTemplate();

    @PostMapping("/appeler")
    public ResponseEntity<?> appelerAgriculteur(@RequestBody DemandeAppel demande) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Accept", "application/json");
        headers.set("apiKey", apiKey);

        MultiValueMap<String, String> corps = new LinkedMultiValueMap<>();
        corps.add("username", username);
        corps.add("to", demande.numero());
        corps.add("from", callerId);

        HttpEntity<MultiValueMap<String, String>> requete = new HttpEntity<>(corps, headers);

        try {
            ResponseEntity<String> reponse = restTemplate.postForEntity(
                "https://voice.africastalking.com/call", requete, String.class);
            return ResponseEntity.ok(reponse.getBody());
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(new ErreurDiagnostic("APPEL_ECHEC", "Impossible de lancer l'appel : " + e.getMessage()));
        }
    }

    record DemandeAppel(String numero) {}
}