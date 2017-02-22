import cn.zjnktion.middleware.objectpool.ObjectFactory;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by zhengjn on 2016/8/23.
 */
public class TestFactory implements ObjectFactory<TestObject>
{
    private static final AtomicInteger ID_GEN = new AtomicInteger(0);

    public TestObject create() throws Exception
    {
        return new TestObject(ID_GEN.incrementAndGet());
    }

    public boolean validate(TestObject obj)
    {
        return true;
    }

    public void destroy(TestObject obj) throws Exception
    {

    }
}
