package rentconfigservice.service.jwt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.*;
import org.springframework.stereotype.Component;
import rentconfigservice.config.properties.JwtProperty;
import rentconfigservice.core.dto.UserDetailsDto;

import java.util.Date;
import java.util.concurrent.TimeUnit;

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
                .setExpiration(new Date(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(1)))
                .signWith(SignatureAlgorithm.HS512, jwtProperty.getSecret())
                .compact();
    }

    public UserDetailsDto getUserDetailsDtoFromJwt(String token) throws JsonProcessingException {
        Claims claims = Jwts.parser()
                .setSigningKey(jwtProperty.getSecret())
                .parseClaimsJws(token)
                .getBody();

        return convertDtoFromJson(claims.getSubject());
    }


    public Date getExpirationDate(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(jwtProperty.getSecret())
                .parseClaimsJws(token)
                .getBody();

        return claims.getExpiration();
    }


    public boolean validate(String token) {
        try {
            Jwts.parser().setSigningKey(jwtProperty.getSecret()).parseClaimsJws(token);
            return true;
        } catch (SignatureException ex) {
            //logger.error("Invalid JWT signature - {}", ex.getMessage());
        } catch (MalformedJwtException ex) {
            //logger.error("Invalid JWT token - {}", ex.getMessage());
        } catch (ExpiredJwtException ex) {
            //logger.error("Expired JWT token - {}", ex.getMessage());
        } catch (UnsupportedJwtException ex) {
            //logger.error("Unsupported JWT token - {}", ex.getMessage());
        } catch (IllegalArgumentException ex) {
            //logger.error("JWT claims string is empty - {}", ex.getMessage());
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
