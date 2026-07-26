package sn.farmerai.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import sn.farmerai.model.Abonnement;
import sn.farmerai.repository.AbonnementRepository;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final RestTemplate restTemplate;
    private final AbonnementRepository abonnementRepository;

    @Value("${paydunya.master-key}")
    private String masterKey;
    @Value("${paydunya.private-key}")
    private String privateKey;
    @Value("${paydunya.public-key}")
    private String publicKey;
    @Value("${paydunya.token}")
    private String token;
    @Value("${app.base-url}")
    private String baseUrl;

    private static final String URL_SANDBOX = "https://app.paydunya.com/sandbox-api/v1/checkout-invoice/create";
    private static final String URL_LIVE = "https://app.paydunya.com/api/v1/checkout-invoice/create";

    public record LienPaiement(String url, String tokenFacture) {}

    public LienPaiement creerAbonnement(String userId, String plan) {
        int montant = plan.equals("annuel") ? 15000 : 1500;

        Map<String, Object> body = Map.of(
                "invoice", Map.of(
                        "total_amount", montant,
                        "description", "Abonnement FarmerAI - " + plan
                ),
                "store", Map.of("name", "FarmerAI"),
                "actions", Map.of(
                        "cancel_url", baseUrl + "/abonnement/annule",
                        "return_url", baseUrl + "/abonnement/succes",
                        "callback_url", baseUrl + "/api/paiements/webhook"
                ),
                "custom_data", Map.of("userId", userId, "plan", plan)
        );

        HttpHeaders headers = new HttpHeaders();
        headers.set("PAYDUNYA-MASTER-KEY", masterKey);
        headers.set("PAYDUNYA-PRIVATE-KEY", privateKey);
        headers.set("PAYDUNYA-PUBLIC-KEY", publicKey);
        headers.set("PAYDUNYA-TOKEN", token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        // Detection automatique du mode selon le prefixe de la cle privee
        String urlAppel = privateKey.startsWith("test_") ? URL_SANDBOX : URL_LIVE;

        @SuppressWarnings("unchecked")
        Map<String, Object> response = restTemplate.postForObject(urlAppel, requestEntity, Map.class);

        String checkoutUrl = (String) response.get("response_text");
        String invoiceToken = (String) response.get("token");

        Abonnement abonnement = new Abonnement();
        abonnement.setUserId(userId);
        abonnement.setPlan(plan);
        abonnement.setStatut("en_attente");
        abonnement.setDateDebut(LocalDateTime.now());
        abonnement.setReferencePaiement(invoiceToken);
        abonnementRepository.save(abonnement);

        return new LienPaiement(checkoutUrl, invoiceToken);
    }
}