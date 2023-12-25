package rentconfigservice.exception;

public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException() {
        super("Для выполнения запроса на данный адрес требуется передать токен авторизации");
    }
}
