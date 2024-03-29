package rentconfigservice.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rentconfigservice.core.entity.TemporarySecretToken;
import rentconfigservice.exception.EntityNotFoundException;
import rentconfigservice.exception.InvalidLinkException;
import rentconfigservice.repository.TemporarySecretTokenRepository;
import rentconfigservice.service.TemporarySecretTokenService;

import java.util.UUID;

@Service
public class TemporarySecretTokenServiceImpl implements TemporarySecretTokenService {

    private final TemporarySecretTokenRepository temporarySecretTokenRepository;

    public TemporarySecretTokenServiceImpl(TemporarySecretTokenRepository temporarySecretTokenRepository) {
        this.temporarySecretTokenRepository = temporarySecretTokenRepository;
    }

    @Transactional
    @Override
    public String createToken(String email) {
        UUID token = UUID.randomUUID();
        TemporarySecretToken entity = new TemporarySecretToken(email, token);
        TemporarySecretToken result = temporarySecretTokenRepository.save(entity);
        return result.getSecretToken().toString();
    }

    @Override
    public String getEmailByToken(String token) {
        String email = temporarySecretTokenRepository.findEmailByToken(UUID.fromString(token));
        if (email == null) {
            throw new InvalidLinkException();
        }
        return email;
    }


    @Transactional
    @Override
    public void deleteEntityByEmailAndToken(String email, String token) {
        if (temporarySecretTokenRepository.deleteEntityByEmailAndToken(email, UUID.fromString(token)) == 0) {
            throw new EntityNotFoundException("User", email);
        }
        ;
    }
}
