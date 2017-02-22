import cn.zjnktion.middleware.objectpool.ObjectPool;
import cn.zjnktion.middleware.objectpool.SynchronizedObjectPool;

/**
 * Created by zhengjn on 2016/8/23.
 */
public class MainTest
{
    public static final ObjectPool<TestObject> pool1 = new SynchronizedObjectPool<TestObject>(new TestFactory());

    static long time;

    public static void main(String[] args)
    {
        TestThread tt = new TestThread();

        int num = 100;
        Thread[] ts = new Thread[num];
        for (int i = 0; i < num; i++)
        {
            ts[i] = new Thread(tt);
        }
        time = System.currentTimeMillis();
        for (Thread t : ts)
        {
            t.start();
        }

    }

    static class TestThread implements Runnable
    {

        public void run()
        {
            for (int i = 0; i < 1000000; i++)
            {
                try
                {

                    TestObject obj = pool1.borrowObject();
                    Thread.yield();
                    //System.out.println(Thread.currentThread().getName() + "===========::" + i + "::===============" + obj.getId());
                    pool1.returnObject(obj);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
            long t = System.currentTimeMillis() - time;
            System.err.println(t);
        }
    }
}
