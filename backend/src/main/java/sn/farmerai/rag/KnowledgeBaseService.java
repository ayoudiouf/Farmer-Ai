package sn.farmerai.rag;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Charge les fiches agronomiques (JSON) au démarrage depuis
 * resources/fiches-agronomiques/. Pour passer à l'échelle (des centaines
 * de fiches INERA/FAO), remplacer par une vraie base vectorielle
 * (ex. pgvector sur la base PostgreSQL existante) - l'interface
 * `rechercher()` de RetrievalService resterait la même côté appelant.
 */
@Service
public class KnowledgeBaseService {

    private final List<FicheAgronomique> fiches = new ArrayList<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostConstruct
    public void chargerFiches() {
        try {
            var resolver = new PathMatchingResourcePatternResolver();
            Resource[] resources = resolver.getResources("classpath:fiches-agronomiques/*.json");

            for (Resource resource : resources) {
                FicheAgronomique fiche = objectMapper.readValue(resource.getInputStream(), FicheAgronomique.class);
                fiches.add(fiche);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Impossible de charger les fiches agronomiques", e);
        }
    }

    public List<FicheAgronomique> toutesLesFiches() {
        return Collections.unmodifiableList(fiches);
    }
}
