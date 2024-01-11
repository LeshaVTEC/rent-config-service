package rentconfigservice.httpclients;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import rentconfigservice.core.dto.UserDetailsDto;
import rentconfigservice.core.dto.audit.AuditDto;
import rentconfigservice.core.entity.UserRole;
import rentconfigservice.service.jwt.JwtHandler;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Component
public class AuditHttpClient {

    private final JwtHandler jwtHandler;
    private final ObjectMapper objectMapper;

    public AuditHttpClient(JwtHandler jwtHandler, ObjectMapper objectMapper) {
        this.jwtHandler = jwtHandler;
        this.objectMapper = objectMapper;
    }

    public AuditDto sendRequestToCreateLog(AuditDto auditDto) {
        try {
            String jwtToken = jwtHandler.generateAccessToken(new UserDetailsDto().setRole(UserRole.SYSTEM));
            String body = objectMapper.writeValueAsString(auditDto);
            HttpClient httpClient = HttpClient.newBuilder().build();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("http://localhost:8081/realty/api/audit"))
                    .headers(
                            "Authorization", "Bearer " + jwtToken,
                            "Content-Type", APPLICATION_JSON_VALUE
                    )
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return objectMapper.readValue(response.body(), AuditDto.class);
        } catch (Exception exception) {
            throw new RuntimeException("Error while sending request to audit-service: " + exception);
        }
    }
}
