package sn.farmerai.rag;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
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

    @Value("${app.anthropic.web-search-max-uses:3}")
    private int webSearchMaxUses;

    private static final String ANTHROPIC_URL = "https://api.anthropic.com/v1/messages";

    public record ReponseConseil(String reponse, List<String> sourcesUtilisees) {}

    public ReponseConseil repondre(String question, String langue) {
        List<FicheAgronomique> fichesPertinentes = retrievalService.rechercher(question, 3);

        if (fichesPertinentes.isEmpty()) {
            // Aucune fiche locale pertinente -> on bascule sur la recherche web en direct
            return repondreAvecRechercheWeb(question, langue);
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

        Map<String, Object> body = Map.of(
                "model", anthropicModel,
                "max_tokens", 500,
                "system", systemPrompt,
                "messages", List.of(Map.of("role", "user", "content", question))
        );

        try {
            Map<String, Object> response = appelerAnthropic(body);
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

    /**
     * Utilisé quand aucune fiche locale ne correspond à la question.
     * Claude peut alors chercher sur le web en temps réel (outil web_search)
     * pour répondre à des questions sur des cultures ou sujets non
     * encore documentés dans notre base (ex: manioc, sésame, prix marché...).
     */
    private ReponseConseil repondreAvecRechercheWeb(String question, String langue) {
        String systemPrompt = """
                Tu es FarmerAI, un assistant agronomique pour les petits agriculteurs du Sénégal.
                Aucune fiche locale vérifiée ne correspond à cette question : tu peux utiliser
                l'outil de recherche web pour trouver une information fiable et à jour
                (privilégie les sources agronomiques sérieuses : ISRA, FAO, CNRA, ministères
                de l'agriculture, instituts de recherche agricole).
                Commence ta réponse par : "Information trouvée en ligne (non issue de notre base locale) — "
                Donne ensuite une réponse simple, concrète et actionnable, adaptée au contexte
                sénégalais si possible.
                Réponds dans la langue demandée : %s.
                Garde tes réponses courtes (4-6 phrases maximum), adaptées à un message WhatsApp/USSD.
                """.formatted(langue);

        Map<String, Object> webSearchTool = Map.of(
                "type", "web_search_20250305",
                "name", "web_search",
                "max_uses", webSearchMaxUses
        );

        Map<String, Object> body = Map.of(
                "model", anthropicModel,
                "max_tokens", 800,
                "system", systemPrompt,
                "messages", List.of(Map.of("role", "user", "content", question)),
                "tools", List.of(webSearchTool)
        );

        try {
            Map<String, Object> response = appelerAnthropic(body);
            String texte = extraireTexte(response);
            List<String> sourcesWeb = extraireSourcesWeb(response);
            return new ReponseConseil(texte, sourcesWeb);
        } catch (RuntimeException e) {
            return new ReponseConseil(
                    "Je n'ai pas trouvé d'information fiable pour répondre à cette question. "
                    + "Contactez un agent agronomique local (ISRA/ANCAR) pour un conseil précis.",
                    List.of()
            );
        }
    }

    private Map<String, Object> appelerAnthropic(Map<String, Object> body) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("x-api-key", anthropicApiKey);
        headers.set("anthropic-version", "2023-06-01");
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        @SuppressWarnings("unchecked")
        Map<String, Object> response = restTemplate.postForObject(ANTHROPIC_URL, requestEntity, Map.class);
        return response;
    }

    @SuppressWarnings("unchecked")
    private String extraireTexte(Map<String, Object> response) {
        if (response == null || !response.containsKey("content")) {
            return "Réponse indisponible.";
        }
        List<Map<String, Object>> content = (List<Map<String, Object>>) response.get("content");
        // On ne garde que les blocs de type "text" : les blocs "server_tool_use"
        // et "web_search_tool_result" sont des étapes internes de recherche,
        // pas du texte destiné à l'utilisateur.
        return content.stream()
                .filter(block -> "text".equals(block.get("type")))
                .map(block -> (String) block.get("text"))
                .collect(Collectors.joining("\n"));
    }

    @SuppressWarnings("unchecked")
    private List<String> extraireSourcesWeb(Map<String, Object> response) {
        List<String> sources = new ArrayList<>();
        if (response == null || !response.containsKey("content")) {
            return sources;
        }
        List<Map<String, Object>> content = (List<Map<String, Object>>) response.get("content");

        for (Map<String, Object> block : content) {
            if ("web_search_tool_result".equals(block.get("type"))) {
                Object contentObj = block.get("content");
                if (contentObj instanceof List<?> results) {
                    for (Object result : results) {
                        if (result instanceof Map<?, ?> resultMap) {
                            Object title = resultMap.get("title");
                            Object url = resultMap.get("url");
                            if (title != null && url != null) {
                                sources.add(title + " (" + url + ")");
                            }
                        }
                    }
                }
            }
        }
        return sources;
    }
}