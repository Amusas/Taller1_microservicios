package com.uniquindio.userservice.client;

import com.uniquindio.userservice.dto.*;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class AuthClient {

    private final WebClient webClient;

    public AuthClient(WebClient.Builder builder) {
        String baseUrl = System.getenv("AUTH_SERVICE_URL");
        if (baseUrl == null || baseUrl.trim().isEmpty()) {
            baseUrl = "http://localhost:8082/api/v1/auth";
        }
        this.webClient = builder.baseUrl(baseUrl).build();
    }

    public String login(LoginRequest loginRequest) {
        ApiDBResponse<String> response = webClient.post()
                .uri("/login")
                .bodyValue(loginRequest)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ApiDBResponse<String>>() {})
                .block();

        return response != null ? response.data() : null;
    }

    public OtpResponse requestOtp(OtpRequest request) {
        ApiDBResponse<OtpResponse> response = webClient.post()
                .uri("/otp-generator")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ApiDBResponse<OtpResponse>>() {})
                .block();

        return response != null ? response.data() : null;
    }

    public Boolean recoverPassword(PasswordRecoveryRequest recoveryRequest) {
        ApiDBResponse<Boolean> response = webClient.post()
                .uri("/password-recovery")
                .bodyValue(recoveryRequest)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ApiDBResponse<Boolean>>() {})
                .block();

        return response != null ? response.data() : null;
    }
}


