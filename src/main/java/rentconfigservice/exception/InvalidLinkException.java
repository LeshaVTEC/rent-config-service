package rentconfigservice.exception;

public class InvalidLinkException extends RuntimeException{

    public InvalidLinkException(){
        super("Ссылка устарела или не действительна");
    }
}
