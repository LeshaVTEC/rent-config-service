package rentconfigservice.service.impl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import rentconfigservice.service.UserPasswordEncoder;

@Component
public class UserPasswordEncoderImpl implements UserPasswordEncoder {

    private final PasswordEncoder passwordEncoder;

    public UserPasswordEncoderImpl(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public String encodePassword(String plainPassword) {
        return passwordEncoder.encode(plainPassword);
    }

    @Override
    public Boolean passwordMatches(String plainPassword, String encodedPassword) {
        return passwordEncoder.matches(plainPassword, encodedPassword);
    }

}
