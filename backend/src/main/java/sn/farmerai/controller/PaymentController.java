package sn.farmerai.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import sn.farmerai.repository.AbonnementRepository;
import sn.farmerai.service.PaymentService;

import java.util.Map;

@RestController
@RequestMapping("/api/paiements")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final AbonnementRepository abonnementRepository;

    public record DemandeAbonnement(String userId, String plan) {}

    @PostMapping("/abonner")
    public PaymentService.LienPaiement abonner(@RequestBody DemandeAbonnement demande) {
        return paymentService.creerAbonnement(demande.userId(), demande.plan());
    }

    @PostMapping("/webhook")
    public String webhook(@RequestParam Map<String, String> params) {
        String invoiceToken = params.get("token");
        String status = params.get("status");

        abonnementRepository.findByReferencePaiement(invoiceToken).ifPresent(abonnement -> {
            if ("completed".equals(status)) {
                abonnement.setStatut("actif");
                int dureeJours = "annuel".equals(abonnement.getPlan()) ? 365 : 30;
                abonnement.setDateFin(java.time.LocalDateTime.now().plusDays(dureeJours));
                abonnementRepository.save(abonnement);
            }
        });

        return "OK";
    }
}