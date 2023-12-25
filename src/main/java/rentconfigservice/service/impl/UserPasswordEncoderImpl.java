package rentconfigservice.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import rentconfigservice.service.UserPasswordEncoder;

@Component
public class UserPasswordEncoderImpl implements UserPasswordEncoder {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public String encodePassword(String plainPassword) {
        return passwordEncoder.encode(plainPassword);
    }

    @Override
    public Boolean passwordMatches(String plainPassword, String encodedPassword) {
        return passwordEncoder.matches(plainPassword, encodedPassword);
    }

}
