package sn.farmerai.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CnnDiagnosticClient {

    private final RestTemplate restTemplate;

    @Value("${app.cnn-service.url}")
    private String cnnServiceUrl;

    public record CnnResult(String maladieDetectee, double indiceConfiance, String recommandation) {}

    public CnnResult analyser(MultipartFile photo, String culture) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", new ByteArrayResource(photo.getBytes()) {
                @Override
                public String getFilename() {
                    return photo.getOriginalFilename();
                }
            });
            body.add("culture", culture);

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            Map<String, Object> response = restTemplate.postForObject(
                    cnnServiceUrl + "/predict", requestEntity, Map.class);

            if (response == null) {
                return fallback();
            }

            return new CnnResult(
                    (String) response.get("maladie_detectee"),
                    ((Number) response.get("indice_confiance")).doubleValue(),
                    (String) response.get("recommandation")
            );
        } catch (IOException | RuntimeException e) {
            // Le microservice CNN peut être indisponible (pas encore déployé, en panne...) -
            // on ne bloque pas l'agriculteur, on renvoie un message clair.
            return fallback();
        }
    }

    private CnnResult fallback() {
        return new CnnResult(
                "service_indisponible",
                0.0,
                "Le service de diagnostic est momentanément indisponible. Réessayez dans quelques minutes."
        );
    }
}
