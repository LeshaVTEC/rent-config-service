package rentconfigservice.service;

public interface TemporarySecretTokenService {

    String createToken(String email);

    String getEmailByToken(String token);

    void deleteEntityByEmailAndToken(String email, String token);

}
