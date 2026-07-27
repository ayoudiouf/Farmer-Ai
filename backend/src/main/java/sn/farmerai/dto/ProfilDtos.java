package sn.farmerai.dto;

public class ProfilDtos {

    public record ProfilResponse(
            String telephone,
            String nomComplet,
            String region,
            String langue,
            String photoUrl
    ) {}

    public record MettreAJourProfilRequest(
            String nomComplet,
            String region,
            String langue
    ) {}

    public record ChangerMotDePasseRequest(
            String ancienMotDePasse,
            String nouveauMotDePasse
    ) {}
}