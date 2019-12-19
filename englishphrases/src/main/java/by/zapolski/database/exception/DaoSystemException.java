package by.zapolski.database.exception;

public class DaoSystemException extends DaoException {
    public DaoSystemException(String message) {
        super(message);
    }

    public DaoSystemException(String message, Throwable cause) {
        super(message, cause);
    }
}
