package sn.farmerai.rag;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import sn.farmerai.service.CnnDiagnosticClient.CnnResult;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClaudeVisionDiagnosticService {

    private final RestTemplate restTemplate;
    private final RagConseilService ragConseilService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${app.anthropic.api-key:}")
    private String anthropicApiKey;

    @Value("${app.anthropic.model:claude-sonnet-5}")
    private String anthropicModel;

    private static final String ANTHROPIC_URL = "https://api.anthropic.com/v1/messages";

    public CnnResult diagnostiquer(MultipartFile photo, String culture) {
        try {
            String imageBase64 = Base64.getEncoder().encodeToString(photo.getBytes());
            String mediaType = photo.getContentType() != null ? photo.getContentType() : "image/jpeg";

            String promptDiagnostic = """
                    Analyse cette photo de plante cultivée en Afrique de l'Ouest (Sénégal).
                    Culture indiquée par l'agriculteur : %s (peut être imprécise ou absente).

                    Réponds UNIQUEMENT en JSON valide, sans texte avant ni après, avec cette
                    structure exacte :
                    {
                      "culture_identifiee": "nom de la culture ou 'incertain'",
                      "diagnostic": "nom de la maladie, carence ou ravageur identifié, ou 'sain' si aucun problème visible",
                      "confiance": "haute" | "moyenne" | "faible",
                      "symptomes_observes": "description courte des symptômes visibles"
                    }

                    Si tu n'es pas sûr, indique une confiance faible plutôt que d'inventer un diagnostic.
                    """.formatted(culture == null || culture.isBlank() ? "non précisée" : culture);

            Map<String, Object> body = Map.of(
                    "model", anthropicModel,
                    "max_tokens", 500,
                    "messages", List.of(Map.of(
                            "role", "user",
                            "content", List.of(
                                    Map.of(
                                            "type", "image",
                                            "source", Map.of(
                                                    "type", "base64",
                                                    "media_type", mediaType,
                                                    "data", imageBase64
                                            )
                                    ),
                                    Map.of("type", "text", "text", promptDiagnostic)
                            )
                    ))
            );

            HttpHeaders headers = new HttpHeaders();
            headers.set("x-api-key", anthropicApiKey);
            headers.set("anthropic-version", "2023-06-01");
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.postForObject(ANTHROPIC_URL, requestEntity, Map.class);

            String texteJson = extraireTexte(response);
            JsonNode diagnosticNode = objectMapper.readTree(texteJson);

            String cultureIdentifiee = diagnosticNode.path("culture_identifiee").asText("incertain");
            String diagnostic = diagnosticNode.path("diagnostic").asText("indetermine");
            String confiance = diagnosticNode.path("confiance").asText("faible");

            double indiceConfiance = switch (confiance) {
                case "haute" -> 0.9;
                case "moyenne" -> 0.6;
                default -> 0.3;
            };

            String recommandation;
            if ("sain".equalsIgnoreCase(diagnostic)) {
                recommandation = "Aucune maladie détectée. Continuez la surveillance régulière de vos plants.";
            } else {
                String question = "Comment traiter " + diagnostic + " sur " + cultureIdentifiee + " ?";
                RagConseilService.ReponseConseil conseil = ragConseilService.repondre(question, "français");
                recommandation = conseil.reponse();
            }

            return new CnnResult(diagnostic, indiceConfiance, recommandation);

        } catch (IOException | RuntimeException e) {
            return new CnnResult(
                    "service_indisponible",
                    0.0,
                    "Le service de diagnostic est momentanément indisponible. Réessayez dans quelques minutes."
            );
        }
    }

    @SuppressWarnings("unchecked")
    private String extraireTexte(Map<String, Object> response) {
        if (response == null || !response.containsKey("content")) {
            return "{}";
        }
        List<Map<String, Object>> content = (List<Map<String, Object>>) response.get("content");
        return content.stream()
                .filter(block -> "text".equals(block.get("type")))
                .map(block -> (String) block.get("text"))
                .collect(Collectors.joining("\n"));
    }
}