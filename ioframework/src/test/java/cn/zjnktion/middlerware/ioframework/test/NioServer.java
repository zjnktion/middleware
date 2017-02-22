package cn.zjnktion.middlerware.ioframework.test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * @author zjnktion
 */
public class NioServer {

    private Selector selector;
    private Acceptor acceptor;
    private Processor processor = new Processor();

    public void bind(int port) throws IOException {
        selector = Selector.open();
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        ServerSocket serverSocket = serverSocketChannel.socket();
        serverSocket.bind(new InetSocketAddress(port));

        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        startupAcceptor();
    }

    private void startupAcceptor() {
        if (acceptor == null) {
            acceptor = new Acceptor();
            Thread t = new Thread(acceptor);
            t.setDaemon(true);
            t.start();
        }
    }

    class Acceptor implements Runnable {

        public void run() {
            while (true) {
                try {
                    selector.select();
                    Set<SelectionKey> keys = selector.selectedKeys();
                    Iterator<SelectionKey> iterator = keys.iterator();
                    while (iterator.hasNext()) {
                        SelectionKey key = iterator.next();
                        iterator.remove();
                        handleAccept(key);
                    }
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void handleAccept(SelectionKey key) throws IOException {
            ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
            SocketChannel socketChannel = serverSocketChannel.accept();
            socketChannel.configureBlocking(false);
            processor.newSession(socketChannel);
        }
    }

    class Processor {

        private List<Session> sessionList = new ArrayList<Session>();
        private Selector selector;

        Processor() {
            try {
                selector = Selector.open();
                Thread t = new Thread(new IOThread());
                t.setDaemon(true);
                t.start();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }

        void newSession(SocketChannel socketChannel) throws IOException {
            Session session = new Session(socketChannel);
            try {
                socketChannel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
            }
            catch (ClosedChannelException e) {
                e.printStackTrace();
            }
            sessionList.add(session);
            System.out.println("accept new channel");
        }

        class Session {

            private SocketChannel socketChannel;

            Session(SocketChannel socketChannel) {
                this.socketChannel = socketChannel;
            }
        }

        class IOThread implements Runnable {

            public void run() {
                while (true) {
                    try {
                        selector.select(10000);
                        Set<SelectionKey> keys = selector.selectedKeys();
                        Iterator<SelectionKey> iterator = keys.iterator();
                        while (iterator.hasNext()) {
                            SelectionKey key = iterator.next();
                            iterator.remove();
                            handleIO(key);
                        }
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            private void handleIO(SelectionKey key) {
                if (key.isReadable()) {
                    System.out.println("read msg :");
                    SocketChannel socketChannel = (SocketChannel) key.channel();
                    ByteBuffer buffer = ByteBuffer.allocate(1024);
                    try {
                        int count = socketChannel.read(buffer);
                        System.out.println(new String(buffer.array(), 0, count));
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                if (key.isWritable()) {
                    System.out.println("writeable");
                }
            }
        }
    }

    public static void main(String[] args) {
        try {
            new NioServer().bind(8765);
            Thread.sleep(10000000L);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
