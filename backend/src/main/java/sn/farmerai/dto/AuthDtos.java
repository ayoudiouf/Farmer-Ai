package sn.farmerai.dto;

import jakarta.validation.constraints.NotBlank;

public class AuthDtos {

    public record RegisterRequest(
        @NotBlank String telephone,
        @NotBlank String nomComplet,
        @NotBlank String motDePasse,
        String region,
        String langue
    ) {}

    public record LoginRequest(
        @NotBlank String telephone,
        @NotBlank String motDePasse
    ) {}

    public record AuthResponse(
        Long id,
        String token,
        String telephone,
        String nomComplet
    ) {}
    public record ForgotPasswordRequest(
    @NotBlank String telephone
    ) {}
}

