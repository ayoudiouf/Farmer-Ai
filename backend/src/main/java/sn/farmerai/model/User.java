package sn.farmerai.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String telephone; // numéro utilisé pour WhatsApp/USSD

    private String nomComplet;

    private String region; // pour conseils agronomiques localisés

    @Enumerated(EnumType.STRING)
    private LangueLocale langue; // FRANCAIS, WOLOF, DIOLA, SERERE

    @Column(nullable = false)
    private String motDePasseHash;

    @Enumerated(EnumType.STRING)
    private Role role; // AGRICULTEUR, COOPERATIVE, ADMIN

    @Builder.Default
    private LocalDateTime dateCreation = LocalDateTime.now();

    public enum LangueLocale { FRANCAIS, WOLOF, DIOLA, SERERE }
    public enum Role { AGRICULTEUR, COOPERATIVE, ADMIN }
}
