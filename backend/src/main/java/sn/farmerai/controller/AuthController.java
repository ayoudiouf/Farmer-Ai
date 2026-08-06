package sn.farmerai.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import sn.farmerai.config.JwtUtil;
import sn.farmerai.dto.AuthDtos.*;
import sn.farmerai.model.User;
import sn.farmerai.repository.UserRepository;
import sn.farmerai.service.SmsService;
import java.util.Random;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final SmsService smsService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest req) {
        if (userRepository.existsByTelephone(req.telephone())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ErreurAuth("TELEPHONE_EXISTANT", "Ce numéro est déjà enregistré."));
        }
        User user = User.builder()
                .telephone(req.telephone())
                .nomComplet(req.nomComplet())
                .region(req.region())
                .langue(req.langue() != null ? User.LangueLocale.valueOf(req.langue()) : User.LangueLocale.FRANCAIS)
                .motDePasseHash(passwordEncoder.encode(req.motDePasse()))
                .role(User.Role.AGRICULTEUR)
                .build();
        userRepository.save(user);

        String token = jwtUtil.generateToken(user.getTelephone());
        return ResponseEntity.ok(new AuthResponse(user.getId(), token, user.getTelephone(), user.getNomComplet()));
    }

    @PostMapping("/forgot-password")
public ResponseEntity<?> forgotPassword(@Valid @RequestBody ForgotPasswordRequest req) {
    User user = userRepository.findByTelephone(req.telephone()).orElse(null);

    if (user == null) {
        return ResponseEntity.status(404)
            .body(new ErreurAuth("UTILISATEUR_INTROUVABLE", "Numéro introuvable."));
    }

    String newPassword = generatePassword();
    user.setMotDePasseHash(passwordEncoder.encode(newPassword));
    userRepository.save(user);

    smsService.sendSms(
        req.telephone(),
        "FarmerAI : Votre mot de passe temporaire est : " + newPassword + ". Changez-le après connexion."
    );

    return ResponseEntity.ok(Map.of("message", "Mot de passe envoyé par SMS"));
}

private String generatePassword() {
    String chars = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghjkmnpqrstuvwxyz23456789";
    StringBuilder sb = new StringBuilder();
    Random random = new Random();
    for (int i = 0; i < 8; i++) {
        sb.append(chars.charAt(random.nextInt(chars.length())));
    }
    return sb.toString();
}

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest req) {
        User user = userRepository.findByTelephone(req.telephone())
                .orElse(null);

        if (user == null || !passwordEncoder.matches(req.motDePasse(), user.getMotDePasseHash())) {
            return ResponseEntity.status(401).body("Numéro ou mot de passe incorrect.");
        }

        String token = jwtUtil.generateToken(user.getTelephone());
        return ResponseEntity.ok(new AuthResponse(user.getId(), token, user.getTelephone(), user.getNomComplet()));
    }

    record ErreurAuth(String code, String message) {}
}