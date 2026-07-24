package sn.farmerai.rag;

public record FicheAgronomique(
        String id,
        String culture,
        String titre,
        String contenu,
        String source
) {}
