package rentconfigservice.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import rentconfigservice.core.entity.TemporarySecretToken;
import rentconfigservice.exception.EntityNotFoundException;
import rentconfigservice.exception.InvalidLinkException;
import rentconfigservice.repository.TemporarySecretTokenRepository;
import rentconfigservice.service.TemporarySecretTokenService;

import java.util.UUID;

@Service
public class TemporarySecretTokenServiceImpl implements TemporarySecretTokenService {

    @Autowired
    private TemporarySecretTokenRepository temporarySecretTokenRepository;

    @Override
    public String createToken(String email){
        UUID token = UUID.randomUUID();
        TemporarySecretToken entity = new TemporarySecretToken(email, token);
        TemporarySecretToken result = temporarySecretTokenRepository.save(entity);
        return result.getSecretToken().toString();
    }

    @Override
    public String getEmailByToken(String token){
        String email = temporarySecretTokenRepository.findEmailByToken(UUID.fromString(token));
        if(email == null){
            throw new InvalidLinkException();
        }
        return email;
    }


    @Override
    public void deleteEntityByEmailAndToken(String email, String token){
        if(temporarySecretTokenRepository.deleteEntityByEmailAndToken(email, UUID.fromString(token)) == 0){
            throw new EntityNotFoundException("User", email);
        };
    }
}
