package cn.zjnktion.middleware.objectpool;

/**
 * @author zjnktion
 */
public interface ObjectPool<T> {

    T createObject() throws Exception;

    T borrowObject() throws Exception;

    void returnObject(T obj) throws Exception;

    void destroyObject(T obj) throws Exception;

    interface PooledObject<T> {
        T getOriginObject();

        boolean setIdle();

        boolean setInuse();

        boolean setInvalidate();

        PooledObjectState getState();

        long getCreateTimeMillis();

        long getLastBorrowTimeMillis();

        long getLastReturnTimeMillis();
    }
}
