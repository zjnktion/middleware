package cn.zjnktion.middleware.ioframework;

/**
 * @author zjnktion
 */
public interface Processor {

    void add(Session session);

    void remove(Session session);
}
