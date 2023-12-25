package rentconfigservice.service;

public interface EmailMessageBuilder {

    String buildVerificationMessage(String email, String token);

    String buildUpdatePasswordMessage(String token);

    String buildVerificationSubject();

    String buildUpdatePasswordSubject();
}
