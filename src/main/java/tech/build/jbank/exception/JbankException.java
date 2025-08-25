package tech.build.jbank.exception;

public abstract class JbankException extends RuntimeException {

    public JbankException(String message) {
        super(message);
    }

    public JbankException(Throwable cause) {
        super(cause);
    }
}
