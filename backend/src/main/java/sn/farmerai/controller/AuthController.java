package sn.farmerai.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import sn.farmerai.config.JwtUtil;
import sn.farmerai.dto.AuthDtos.*;
import sn.farmerai.model.User;
import sn.farmerai.repository.UserRepository;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest req) {
        if (userRepository.existsByTelephone(req.telephone())) {
            return ResponseEntity.badRequest().body("Un compte existe déjà avec ce numéro.");
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
        return ResponseEntity.ok(new AuthResponse(token, user.getTelephone(), user.getNomComplet()));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest req) {
        User user = userRepository.findByTelephone(req.telephone())
                .orElse(null);

        if (user == null || !passwordEncoder.matches(req.motDePasse(), user.getMotDePasseHash())) {
            return ResponseEntity.status(401).body("Numéro ou mot de passe incorrect.");
        }

        String token = jwtUtil.generateToken(user.getTelephone());
        return ResponseEntity.ok(new AuthResponse(token, user.getTelephone(), user.getNomComplet()));
    }
}
