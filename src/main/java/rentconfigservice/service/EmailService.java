package rentconfigservice.service;

public interface EmailService {

    void sendSimpleMessage(String emailTo, String subject, String text);
}
