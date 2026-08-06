package sn.farmerai.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.Base64;
import java.util.Map;

@Service
public class SmsService {

    @Value("${orange.client-id}")
    private String clientId;

    @Value("${orange.client-secret}")
    private String clientSecret;

    @Value("${orange.sender-number}")
    private String senderNumber;

    private final RestTemplate restTemplate = new RestTemplate();

    public void sendSms(String telephone, String message) {
        String accessToken = getAccessToken();

        String encodedSender = "tel%3A%2B" + senderNumber.replace("+", "");
        String url = "https://api.orange.com/smsmessaging/v1/outbound/" + encodedSender + "/requests";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);

        Map<String, Object> body = Map.of(
            "outboundSMSMessageRequest", Map.of(
                "address", "tel:" + telephone,
                "senderAddress", "tel:" + senderNumber,
                "outboundSMSTextMessage", Map.of("message", message)
            )
        );

        restTemplate.postForEntity(url, new HttpEntity<>(body, headers), String.class);
    }

    private String getAccessToken() {
        String credentials = Base64.getEncoder()
            .encodeToString((clientId + ":" + clientSecret).getBytes());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Authorization", "Basic " + credentials);

        ResponseEntity<Map> response = restTemplate.postForEntity(
            "https://api.orange.com/oauth/v3/token",
            new HttpEntity<>("grant_type=client_credentials", headers),
            Map.class
        );

        return (String) response.getBody().get("access_token");
    }
}