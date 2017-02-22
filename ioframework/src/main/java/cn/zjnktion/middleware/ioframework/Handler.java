package cn.zjnktion.middleware.ioframework;

/**
 * @author zjnktion
 */
public interface Handler {

    void sessionCreated(Session session) throws Exception;

    void sessionOpened(Session session) throws Exception;

    void sessionIdle(Session session, IdleType idleType) throws Exception;

    void sessionClosed(Session session) throws Exception;

    void sessionRead(Session session, Object message) throws Exception;

    void sessionWrote(Session session, Object message) throws Exception;

    void exceptionCaught(Session session, Throwable cause) throws Exception;
}
