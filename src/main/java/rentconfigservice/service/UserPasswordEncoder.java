package rentconfigservice.service;

public interface UserPasswordEncoder {

    String encodePassword(String plainPassword);

    Boolean passwordMatches(String plainPassword, String encodedPassword);
}
