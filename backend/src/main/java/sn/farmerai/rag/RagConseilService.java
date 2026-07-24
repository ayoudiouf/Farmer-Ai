package sn.farmerai.rag;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RagConseilService {

    private final RetrievalService retrievalService;
    private final RestTemplate restTemplate;

    @Value("${app.anthropic.api-key:}")
    private String anthropicApiKey;

    @Value("${app.anthropic.model:claude-sonnet-4-6}")
    private String anthropicModel;

    private static final String ANTHROPIC_URL = "https://api.anthropic.com/v1/messages";

    public record ReponseConseil(String reponse, List<String> sourcesUtilisees) {}

    public ReponseConseil repondre(String question, String langue) {
        List<FicheAgronomique> fichesPertinentes = retrievalService.rechercher(question, 3);

        if (fichesPertinentes.isEmpty()) {
            return new ReponseConseil(
                    "Je n'ai pas assez d'information dans ma base pour répondre précisément à cette question. "
                    + "Contactez un agent agronomique local ou reformulez votre question.",
                    List.of()
            );
        }

        String contexte = fichesPertinentes.stream()
                .map(f -> "[" + f.titre() + " - " + f.source() + "]\n" + f.contenu())
                .collect(Collectors.joining("\n\n"));

        String systemPrompt = """
                Tu es FarmerAI, un assistant agronomique pour les petits agriculteurs du Sénégal.
                Réponds UNIQUEMENT à partir du contexte fourni ci-dessous, de façon simple, concrète et actionnable.
                Si le contexte ne permet pas de répondre, dis-le clairement plutôt que d'inventer.
                Réponds dans la langue demandée : %s.
                Garde tes réponses courtes (3-5 phrases maximum), adaptées à un message WhatsApp/USSD.

                Contexte agronomique disponible :
                %s
                """.formatted(langue, contexte);

        HttpHeaders headers = new HttpHeaders();
        headers.set("x-api-key", anthropicApiKey);
        headers.set("anthropic-version", "2023-06-01");
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = Map.of(
                "model", anthropicModel,
                "max_tokens", 500,
                "system", systemPrompt,
                "messages", List.of(Map.of("role", "user", "content", question))
        );

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.postForObject(ANTHROPIC_URL, requestEntity, Map.class);
            String texte = extraireTexte(response);
            List<String> sources = fichesPertinentes.stream().map(FicheAgronomique::titre).toList();
            return new ReponseConseil(texte, sources);
        } catch (RuntimeException e) {
            return new ReponseConseil(
                    "Le service de conseil est momentanément indisponible. Réessayez dans quelques minutes.",
                    List.of()
            );
        }
    }

    @SuppressWarnings("unchecked")
    private String extraireTexte(Map<String, Object> response) {
        if (response == null || !response.containsKey("content")) {
            return "Réponse indisponible.";
        }
        List<Map<String, Object>> content = (List<Map<String, Object>>) response.get("content");
        return content.stream()
                .filter(block -> "text".equals(block.get("type")))
                .map(block -> (String) block.get("text"))
                .collect(Collectors.joining("\n"));
    }
}
