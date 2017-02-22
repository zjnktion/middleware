package cn.zjnktion.middleware.ioframework;

/**
 * @author zjnktion
 */
public class ExceptionSupervisor {

    private static final ExceptionSupervisor EXCEPTION_SUPERVISOR = new ExceptionSupervisor();

    private ExceptionSupervisor() {

    }

    public static ExceptionSupervisor getInstance() {
        return EXCEPTION_SUPERVISOR;
    }

    public void exceptionCaught(Throwable cause) {
        if (cause instanceof Error) {
            throw (Error) cause;
        }

        cause.printStackTrace();
    }

}
