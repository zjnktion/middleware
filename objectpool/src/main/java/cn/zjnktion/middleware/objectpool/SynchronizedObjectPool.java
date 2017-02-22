package cn.zjnktion.middleware.objectpool;

import java.util.HashMap;
import java.util.NoSuchElementException;

/**
 * @author zjnktion
 */
public class SynchronizedObjectPool<T> implements ObjectPool<T> {

    // --- 配置属性 -----------------------------------------------------------------------------------------------------
    private final int maxTotal;
    private final boolean blockWhenResourceShortage;
    private final long maxBlockMillis;
    private final boolean retryWhileCheckOutValidateFail;
    private final long maxIdleValidateMillis;

    // --- 基本字段 -----------------------------------------------------------------------------------------------------
    private final ObjectFactory<T> objectFactory;

    private final HashMap<T, ObjectPool.PooledObject<T>> managedObjects = new HashMap<T, ObjectPool.PooledObject<T>>();
    private int managedCount = 0;

    private final ObjectPool.PooledObject<T>[] idleObjects;
    private int index = -1;

    // --- 构造方法 -----------------------------------------------------------------------------------------------------
    public SynchronizedObjectPool(ObjectFactory<T> objectFactory)
    {
        this(objectFactory, new ObjectPoolConfig());
    }

    public SynchronizedObjectPool(ObjectFactory<T> objectFactory, ObjectPoolConfig config)
    {
        if (objectFactory == null)
        {
            throw new IllegalArgumentException("object factory can not be null.");
        }

        // 设置配置属性
        this.maxTotal = config.getMaxTotal();
        this.blockWhenResourceShortage = config.isBlockWhenResourceShortage();
        this.maxBlockMillis = config.getMaxBlockMillis();
        this.retryWhileCheckOutValidateFail = config.isRetryWhileCheckOutValidateFail();
        this.maxIdleValidateMillis = config.getMaxIdleValidateMillis();

        // 设置基本字段
        this.objectFactory = objectFactory;
        this.idleObjects = new ObjectPool.PooledObject[this.maxTotal];
    }

    // --- 实现方法 -----------------------------------------------------------------------------------------------------
    public synchronized T createObject() throws Exception {
        ObjectPool.PooledObject<T> item = createInternal();

        if (item == null)
        {
            throw new IllegalStateException("The object tried to create is not in a correct status.");
        }

        return item.getOriginObject();
    }

    public synchronized T borrowObject() throws Exception {
        ObjectPool.PooledObject<T> item = null;

        while (item == null)
        {
            if (index < 0)
            {
                item = createInternal();
                if (item == null)
                {
                    // 资源紧缺
                    if (blockWhenResourceShortage)
                    {
                        // 若设置了资源紧缺则等待直到取到或者超时(非精确超时)
                        if (maxBlockMillis <= 0)
                        {
                            while (index < 0)
                            {
                                wait();
                            }
                        }
                        else
                        {
                            long localWaitMillis = 0L;
                            while (index < 0)
                            {
                                long loopStartMillis = System.currentTimeMillis();
                                if (localWaitMillis < maxBlockMillis)
                                {
                                    wait(maxBlockMillis - localWaitMillis);
                                    localWaitMillis += System.currentTimeMillis() - loopStartMillis;
                                }
                                else
                                {
                                    throw new NoSuchElementException("Resource shortage after wait.");
                                }
                            }
                        }
                        item = idleObjects[index];
                        idleObjects[index--] = null;
                    }
                    else
                    {
                        throw new NoSuchElementException("Resource shortage without wait.");
                    }
                }
            }
            else
            {
                item = idleObjects[index];
                idleObjects[index--] = null;
            }

            if (needValidateBeforeCheckOut(item))
            {
                // 校验对象
                boolean validated = false;
                Throwable validateException = null;
                try
                {
                    validated = objectFactory.validate(item.getOriginObject());
                }
                catch (Throwable e)
                {
                    // 避免未知异常导致线程问题
                    validateException = e;
                }
                if (!validated)
                {
                    try
                    {
                        destroyInternal(item);
                    }
                    catch (Exception e)
                    {
                        // do nothing
                    }
                    item = null;
                    if (!retryWhileCheckOutValidateFail)
                    {
                        NoSuchElementException nsee = new NoSuchElementException("Validate fail while checking out.");
                        nsee.initCause(validateException);
                        throw nsee;
                    }
                }
            }

            // 设置对象借出使用信息
            if (item != null)
            {
                if (!item.setInuse())
                {
                    // 正常来说，不会出现这种情况，但是为了避免不可控自己菜的原因或许会导致的并发bug而加上的一段
                    item = null;
                }
            }
        }

        return item.getOriginObject();
    }

    public synchronized void returnObject(T obj) throws Exception {
        ObjectPool.PooledObject<T> item = managedObjects.get(obj);

        if (item == null)
        {
            throw new IllegalArgumentException("The object tried to check in not a part of this object pool.");
        }

        // 设置对象偿还释放信息
        if (!item.setIdle())
        {
            throw new IllegalStateException("The object tried to check in not in a correct status.");
        }

        this.idleObjects[++index] = item;

        this.notifyAll();
    }

    public synchronized void destroyObject(T obj) throws Exception {
        ObjectPool.PooledObject<T> item = managedObjects.get(obj);

        if (item == null)
        {
            throw new IllegalArgumentException("The object tried to destroy not a part of this object pool.");
        }

        destroyInternal(item);
    }

    static class PooledObject<T> implements ObjectPool.PooledObject<T> {

        private T originObject;

        private final long createTimeMillis = System.currentTimeMillis();
        private long lastBorrowTimeMillis = createTimeMillis;
        private long lastReturnTimeMillis = createTimeMillis;
        private PooledObjectState state = PooledObjectState.IDLE;

        PooledObject(T obj) {
            originObject = obj;
        }

        public T getOriginObject() {
            return originObject;
        }

        public boolean setIdle() {
            if (state != PooledObjectState.INUSE) {
                return false;
            }

            lastReturnTimeMillis = System.currentTimeMillis();
            state = PooledObjectState.IDLE;
            return true;
        }

        public boolean setInuse() {
            if (state != PooledObjectState.IDLE) {
                return false;
            }

            lastBorrowTimeMillis = System.currentTimeMillis();
            state = PooledObjectState.INUSE;
            return true;
        }

        public boolean setInvalidate() {
            if (state != PooledObjectState.IDLE) {
                return false;
            }

            state = PooledObjectState.INVALIDATE;
            return true;
        }

        public PooledObjectState getState() {
            return state;
        }

        public long getCreateTimeMillis() {
            return createTimeMillis;
        }

        public long getLastBorrowTimeMillis() {
            return lastBorrowTimeMillis;
        }

        public long getLastReturnTimeMillis() {
            return lastReturnTimeMillis;
        }
    }

    // --- 私有方法 -----------------------------------------------------------------------------------------------------
    private ObjectPool.PooledObject<T> createInternal()
    {
        if (managedCount == Integer.MAX_VALUE || managedCount == maxTotal)
        {
            return null;
        }

        ObjectPool.PooledObject<T> item;
        try
        {
            T obj = objectFactory.create();
            item = new PooledObject<T>(obj);
            managedObjects.put(item.getOriginObject(), item);
            this.managedCount++;
            return item;
        }
        catch (Exception e)
        {
            return null;
        }
    }

    private void destroyInternal(ObjectPool.PooledObject<T> item) throws Exception
    {

        if (!item.setInvalidate()) {
            throw new IllegalStateException("Can not invalidate item because current state is :[" + item.getState() + "]");
        }

        if (index >= 0)
        {
            for (int i = 0; i <= index; i++)
            {
                if (idleObjects[i] == item)
                {
                    int moveNum = index - i;
                    if (moveNum > 0)
                    {
                        System.arraycopy(idleObjects, i + 1, idleObjects, i, moveNum);
                    }
                    idleObjects[index--] = null;
                    break;
                }
            }
        }

        managedObjects.remove(item.getOriginObject());
        try
        {
            objectFactory.destroy(item.getOriginObject());
        }
        finally
        {
            this.managedCount--;
        }
    }

    private boolean needValidateBeforeCheckOut(ObjectPool.PooledObject<T> item)
    {
        return this.maxIdleValidateMillis >= 0 && System.currentTimeMillis() - item.getLastReturnTimeMillis() >= this.maxIdleValidateMillis;
    }
}
