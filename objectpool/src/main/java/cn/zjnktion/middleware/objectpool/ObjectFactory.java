package cn.zjnktion.middleware.objectpool;

/**
 * @author zjnktion
 */
public interface ObjectFactory<T> {

    T create() throws Exception;

    boolean validate(T obj);

    void destroy(T obj) throws Exception;
}
