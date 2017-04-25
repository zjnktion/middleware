package cn.zjnktion.middleware.zson.exception;

/**
 * @author zjnktion
 */
public class RuntimeIOException extends RuntimeException {

    public RuntimeIOException() {
        super();
    }

    public RuntimeIOException(String msg) {
        super(msg);
    }

    public RuntimeIOException(Throwable cause) {
        super(cause);
    }

    public RuntimeIOException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
