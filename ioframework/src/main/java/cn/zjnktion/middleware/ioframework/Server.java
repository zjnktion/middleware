package cn.zjnktion.middleware.ioframework;

import java.io.IOException;
import java.net.SocketAddress;
import java.util.Set;

/**
 * @author zjnktion
 */
public interface Server {

    Set<SocketAddress> getBoundAddresses();

    void setHandler(Handler handler);

    Handler getHandler();

    void bind(SocketAddress socketAddress) throws IOException;

    void unbind(SocketAddress socketAddress);

    interface BindFuture {

        SocketAddress getSocketAddress();

        void setDone();

        Exception getException();

        void setException(Exception e);

        BindFuture awaitUninterruptibly();
    }
}
