package cn.zjnktion.middleware.ioframework.nio;

import cn.zjnktion.middleware.ioframework.*;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketAddress;
import java.nio.channels.*;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author zjnktion
 */
public class NioTcpServer implements Server {

    private static final int DEFAULT_IO_THREADS = 4 * Runtime.getRuntime().availableProcessors();
    private int ioThreads;

    private Processor[] processors;

    private Selector selector;
    private boolean selectable;

    private Set<SocketAddress> boundAddresses = new CopyOnWriteArraySet<SocketAddress>();

    private Handler handler;

    private final Queue<Server.BindFuture> bindQueue = new ConcurrentLinkedQueue<Server.BindFuture>();
    private final Queue<Server.BindFuture> unbindQueue = new ConcurrentLinkedQueue<Server.BindFuture>();

    private final Map<SocketAddress, ServerSocketChannel> managedServerSocketChannel = new HashMap<SocketAddress, ServerSocketChannel>();

    public NioTcpServer() {
        this(DEFAULT_IO_THREADS);
    }

    public NioTcpServer(int ioThreads) {
        this.ioThreads = ioThreads;

        processors = new NioTcpProcessor[ioThreads];

        for (int i = 0; i < ioThreads; i++) {
            processors[i] = new NioTcpProcessor();
        }

        try {
            selector = Selector.open();
            selectable = true;
            startAcceptor();
        }
        catch (IOException e) {
            throw new RuntimeIOException("Failed to open selector on acceptor.", e);
        }
        finally {
            if (!selectable) {
                try {
                    if (selector != null) {
                        selector.close();
                    }
                }
                catch (Exception e) {
                    ExceptionSupervisor.getInstance().exceptionCaught(e);
                }
            }
        }
    }

    public Set<SocketAddress> getBoundAddresses() {
        return Collections.unmodifiableSet(boundAddresses);
    }

    public void setHandler(Handler handler) {
        if (handler == null) {
            throw new IllegalArgumentException("Can not set a null handler.");
        }
        this.handler = handler;
    }

    public Handler getHandler() {
        return handler;
    }

    public void bind(SocketAddress socketAddress) throws IOException {
        if (socketAddress == null) {
            throw new IllegalArgumentException("Can not bind null socketAddress.");
        }

        if (handler == null) {
            throw new IllegalStateException("Can not bind without handler, please call setHandler(Handler handler) method to set a handler before binding.");
        }

        try {
            SocketAddress newBoundAddress = bind0(socketAddress);

            boundAddresses.add(newBoundAddress);
        }
        catch (IOException e) {
            throw e;
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (Exception e) {
            throw new RuntimeIOException("Can not bind SocketAddress.", e);
        }
    }

    public void unbind(SocketAddress socketAddress) {
        if (socketAddress == null) {
            throw new IllegalArgumentException("Can not unbind null SocketAddress.");
        }

        try {
            unbind0(socketAddress);
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (Exception e) {
            throw new RuntimeIOException("Failed to unbind", e);
        }

        boundAddresses.remove(socketAddress);
    }

    private static class BindFuture implements Server.BindFuture {

        private final SocketAddress socketAddress;

        private boolean done = false;

        private Exception e;

        public BindFuture(SocketAddress socketAddress) {
            this.socketAddress = socketAddress;
        }

        public SocketAddress getSocketAddress() {
            return socketAddress;
        }

        public synchronized void setDone() {
            if (done) {
                return;
            }

            done = true;
            notifyAll();
        }

        public Exception getException() {
            return e;
        }

        public synchronized void setException(Exception e) {
            if (e == null) {
                throw new IllegalArgumentException("Can not set a null exception to BindFuture.");
            }
            this.e = e;
            setDone();
        }

        public synchronized Server.BindFuture awaitUninterruptibly() {
            for (; ; ) {
                if (done) {
                    return this;
                }
                try {
                    wait();
                }
                catch (InterruptedException e) {
                    // do nothing
                }
            }
        }
    }

    private void startAcceptor() {
        Thread starterThread = new Thread(new Acceptor());
        starterThread.setName("Accept-Thread");
        starterThread.start();
    }

    private class Acceptor implements Runnable {

        public void run() {
            while (selectable) {
                try {
                    int selected = selector.select();

                    if (selected > 0) {
                        Set<SelectionKey> keySet = selector.selectedKeys();
                        handleKeys(keySet.iterator());
                    }

                    handleBind();
                    handleUnbind();
                }
                catch (ClosedSelectorException e) {
                    ExceptionSupervisor.getInstance().exceptionCaught(e);
                    break;
                }
                catch (Exception e) {
                    ExceptionSupervisor.getInstance().exceptionCaught(e);
                    try {
                        // sleep 500ms for next select
                        Thread.sleep(500L);
                    }
                    catch (InterruptedException e1) {
                        ExceptionSupervisor.getInstance().exceptionCaught(e);
                    }
                }
            }
        }
    }

    private SocketAddress bind0(SocketAddress socketAddress) throws Exception {
        // Add a BindFuture to bind queue
        Server.BindFuture bindFuture = new BindFuture(socketAddress);
        bindQueue.add(bindFuture);

        // wake up selector
        selector.wakeup();

        bindFuture.awaitUninterruptibly();

        if (bindFuture.getException() != null) {
            throw bindFuture.getException();
        }

        return bindFuture.getSocketAddress();
    }

    private void unbind0(SocketAddress socketAddress) throws Exception {
        Server.BindFuture unbindFuture = new BindFuture(socketAddress);
        unbindQueue.add(unbindFuture);

        selector.wakeup();

        unbindFuture.awaitUninterruptibly();

        if (unbindFuture.getException() != null) {
            throw unbindFuture.getException();
        }
    }

    private void handleKeys(Iterator<SelectionKey> iterator) {
        while (iterator.hasNext()) {
            SelectionKey key = iterator.next();
            iterator.remove();
            try {
                Session session = acceptKey(key);

                if (session == null) {
                    continue;
                }

                allocateSession(session);
            }
            catch (Exception e) {
                ExceptionSupervisor.getInstance().exceptionCaught(e);
            }
        }
    }

    private Session acceptKey(SelectionKey key) throws Exception {
        if (key == null || (!key.isValid()) || (!key.isAcceptable())) {
            return null;
        }

        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();

        SocketChannel socketChannel = serverSocketChannel.accept();

        if (socketChannel == null) {
            return null;
        }

        return new NioTcpSession(socketChannel, getHandler());
    }

    private void allocateSession(Session session) {
        int absId = Math.abs(session.getId());
        processors[absId % ioThreads].add(session);
    }

    private void handleBind() {
        for (; ; ) {
            Server.BindFuture bindFuture = bindQueue.poll();

            if (bindFuture == null) {
                return;
            }

            SocketAddress socketAddress = bindFuture.getSocketAddress();

            try {
                ServerSocketChannel serverSocketChannel = openServerSocketChannel(socketAddress);
                managedServerSocketChannel.put(socketAddress, serverSocketChannel);
            }
            catch (Exception e) {
                bindFuture.setException(e);
            }
            bindFuture.setDone();
        }
    }

    private ServerSocketChannel openServerSocketChannel(SocketAddress socketAddress) throws Exception {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();

        boolean success = false;
        try {
            serverSocketChannel.configureBlocking(false);

            ServerSocket serverSocket = serverSocketChannel.socket();

            serverSocket.bind(socketAddress);

            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            success = true;
        }
        finally {
            if (!success) {
                closeServerSocketChannel(serverSocketChannel);
            }
        }
        return serverSocketChannel;
    }

    private void closeServerSocketChannel(ServerSocketChannel serverSocketChannel) throws Exception {
        SelectionKey key = serverSocketChannel.keyFor(selector);

        if (key != null) {
            key.cancel();
        }

        serverSocketChannel.close();
    }

    private void handleUnbind() {
        for (; ; ) {
            Server.BindFuture unbindFuture = unbindQueue.poll();

            if (unbindFuture == null) {
                return;
            }

            ServerSocketChannel serverSocketChannel = managedServerSocketChannel.remove(unbindFuture.getSocketAddress());

            try {
                if (serverSocketChannel != null) {
                    closeServerSocketChannel(serverSocketChannel);
                }
            }
            catch (Exception e) {
                unbindFuture.setException(e);
            }
            unbindFuture.setDone();
        }
    }

}
