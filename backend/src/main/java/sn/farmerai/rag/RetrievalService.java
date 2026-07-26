package sn.farmerai.rag;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Recherche par mots-clés (simple mais efficace pour un petit corpus de
 * fiches). Suffisant pour le MVP ; à remplacer par une recherche
 * vectorielle (embeddings + similarité cosinus) quand le corpus dépassera
 * quelques centaines de fiches.
 */
@Service
@RequiredArgsConstructor
public class RetrievalService {

    private final KnowledgeBaseService knowledgeBaseService;

    private static final Set<String> MOTS_VIDES = Set.of(
            "le", "la", "les", "de", "des", "du", "un", "une", "et", "pour",
            "comment", "quand", "quel", "quelle", "est", "ce", "que", "quoi",
            "mon", "ma", "mes", "je", "j'ai"
    );

    public List<FicheAgronomique> rechercher(String question, int topK) {
        List<String> motsQuestion = extraireMotsCles(question);

        return knowledgeBaseService.toutesLesFiches().stream()
                .map(fiche -> Map2.of(fiche, score(fiche, motsQuestion, question)))
                .filter(entry -> entry.score() > 0)
                .sorted(Comparator.comparingInt(Map2::score).reversed())
                .limit(topK)
                .map(Map2::fiche)
                .collect(Collectors.toList());
    }

    private int score(FicheAgronomique fiche, List<String> motsQuestion, String questionOriginale) {
        String texte = (fiche.titre() + " " + fiche.contenu()).toLowerCase(Locale.FRENCH);
        String culture = fiche.culture().toLowerCase(Locale.FRENCH);
        int score = 0;

        // Gros bonus si la culture de la fiche est explicitement citée dans la question
        if (contientMotEntier(questionOriginale.toLowerCase(Locale.FRENCH), culture)) {
            score += 10;
        }

        for (String mot : motsQuestion) {
            if (contientMotEntier(texte, mot)) {
                score++;
            }
        }
        return score;
    }

    private boolean contientMotEntier(String texte, String mot) {
        return Pattern.compile("\\b" + Pattern.quote(mot) + "\\b")
                .matcher(texte)
                .find();
    }

    private List<String> extraireMotsCles(String question) {
        return List.of(question.toLowerCase(Locale.FRENCH).split("\\s+")).stream()
                .filter(mot -> mot.length() > 2 && !MOTS_VIDES.contains(mot))
                .collect(Collectors.toList());
    }

    private record Map2(FicheAgronomique fiche, int score) {
        static Map2 of(FicheAgronomique fiche, int score) {
            return new Map2(fiche, score);
        }
    }
}