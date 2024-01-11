package rentconfigservice.service.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import rentconfigservice.config.properties.JwtProperty;
import rentconfigservice.core.dto.UserDetailsDto;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class JwtHandler {

    private final JwtProperty jwtProperty;
    private final ObjectMapper objectMapper;

    public JwtHandler(JwtProperty jwtProperty, ObjectMapper objectMapper) {
        this.jwtProperty = jwtProperty;
        this.objectMapper = objectMapper;
    }

    public String generateAccessToken(UserDetailsDto userDetailsDto) {
        return Jwts.builder()
                .setSubject(convertDtoToJson(userDetailsDto))
                .setIssuer(jwtProperty.getIssuer())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(10)))
                .signWith(SignatureAlgorithm.HS512, jwtProperty.getSecret())
                .compact();
    }

    public UserDetailsDto getUserDetailsDtoFromJwt(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(jwtProperty.getSecret())
                .parseClaimsJws(token)
                .getBody();

        return convertDtoFromJson(claims.getSubject());
    }

    public boolean validate(String token) {
        try {
            Jwts.parser().setSigningKey(jwtProperty.getSecret()).parseClaimsJws(token);
            return true;
        } catch (SignatureException exception) {
            log.error("Invalid JWT signature " + exception.getMessage());
        } catch (MalformedJwtException exception) {
            log.error("Invalid JWT token " + exception.getMessage());
        } catch (ExpiredJwtException exception) {
            log.error("Expired JWT token " + exception.getMessage());
        } catch (UnsupportedJwtException exception) {
            log.error("Unsupported JWT token " + exception.getMessage());
        } catch (IllegalArgumentException exception) {
            log.error("JWT claims string is empty " + exception.getMessage());
        }
        return false;
    }

    private String convertDtoToJson(UserDetailsDto object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (Exception exception) {
            throw new RuntimeException("Object mapper filed while writing value: " + exception.getMessage());
        }
    }

    private UserDetailsDto convertDtoFromJson(String json) {
        try {
            return objectMapper.readValue(json, UserDetailsDto.class);
        } catch (Exception exception) {
            throw new RuntimeException("Object mapper filed while reading value: " + exception.getMessage());
        }
    }
}
