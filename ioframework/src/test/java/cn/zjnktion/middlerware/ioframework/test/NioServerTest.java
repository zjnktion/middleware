package cn.zjnktion.middlerware.ioframework.test;

import cn.zjnktion.middleware.ioframework.Handler;
import cn.zjnktion.middleware.ioframework.IdleType;
import cn.zjnktion.middleware.ioframework.Server;
import cn.zjnktion.middleware.ioframework.Session;
import cn.zjnktion.middleware.ioframework.nio.NioTcpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Date;
import java.util.Set;

/**
 * @author zjnktion
 */
public class NioServerTest {

    public static void main(String[] args) {
        Server server = new NioTcpServer();
        server.setHandler(new Handler() {
            public void sessionCreated(Session session) throws Exception {

            }

            public void sessionOpened(Session session) throws Exception {

            }

            public void sessionIdle(Session session, IdleType idleType) throws Exception {

            }

            public void sessionClosed(Session session) throws Exception {

            }

            public void sessionRead(Session session, Object message) throws Exception {

            }

            public void sessionWrote(Session session, Object message) throws Exception {

            }

            public void exceptionCaught(Session session, Throwable cause) throws Exception {

            }
        });

        try {
            server.bind(new InetSocketAddress(8765));
            System.out.println("8765");

            server.bind(new InetSocketAddress(8766));
            System.out.println("8766");

            server.bind(new InetSocketAddress(8767));
            System.out.println("8767");

            Set<SocketAddress> boundAddresses = server.getBoundAddresses();
            SocketAddress socketAddress = boundAddresses.iterator().next();
            server.unbind(socketAddress);

            System.out.println("-" + socketAddress);
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        Date now = new Date();

        System.out.println(new Date(now.getTime() - 1800 * 1000));
    }
}
