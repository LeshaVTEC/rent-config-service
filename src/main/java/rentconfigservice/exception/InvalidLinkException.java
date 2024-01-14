package rentconfigservice.exception;

public class InvalidLinkException extends RuntimeException {

    public InvalidLinkException() {
        super("Invalid link");
    }
}
