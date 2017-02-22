package cn.zjnktion.middleware.ioframework.nio;

import cn.zjnktion.middleware.ioframework.ExceptionSupervisor;
import cn.zjnktion.middleware.ioframework.Processor;
import cn.zjnktion.middleware.ioframework.RuntimeIOException;
import cn.zjnktion.middleware.ioframework.Session;

import java.io.IOException;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author zjnktion
 */
public class NioTcpProcessor implements Processor {

    private static final AtomicInteger ID_GEN = new AtomicInteger(0);
    private final int id;

    private Selector selector;
    private boolean selectable;

    private final Queue<Session> addSessionQueue = new ConcurrentLinkedQueue<Session>();
    private final Queue<Session> removeSessionQueue = new ConcurrentLinkedQueue<Session>();

    public NioTcpProcessor() {
        try {
            selector = Selector.open();
            id = ID_GEN.incrementAndGet();
            selectable = true;
            startProcessor();
        }
        catch (IOException e) {
            throw new RuntimeIOException("Failed to open selector on processor.", e);
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

    public void add(Session session) {
        addSessionQueue.add(session);
    }

    public void remove(Session session) {
        removeSessionQueue.add(session);
    }

    private void startProcessor() {
        Thread t = new Thread(new Processor());
        t.setName("IO-Processor-" + id);
        t.setDaemon(true);
        t.start();
    }

    private class Processor implements Runnable {

        public void run() {
            while (selectable) {
                try {
                    int selected = selector.select(1000L);

                    if (selected > 0) {
                        process();
                    }

                    addSessions();
                    removeSessions();
                }
                catch (ClosedSelectorException e) {
                    ExceptionSupervisor.getInstance().exceptionCaught(e);
                    break;
                }
                catch (Exception e) {
                    ExceptionSupervisor.getInstance().exceptionCaught(e);
                    try {
                        Thread.sleep(50L);
                    }
                    catch (InterruptedException e1) {
                        ExceptionSupervisor.getInstance().exceptionCaught(e1);
                    }
                }
            }
        }
    }

    private void process() throws Exception {
        for (Iterator<SelectionKey> iterator = selector.selectedKeys().iterator(); iterator.hasNext(); ) {
            SelectionKey key = iterator.next();
            iterator.remove();

            Session session = (Session) key.attachment();
            process(session);
        }
    }

    private void process(Session session) {
        SelectionKey key = session.getSelectionKey();

        if (key != null && key.isValid() && key.isReadable()) {
            read(session);
        }

        // todo 还要加上session本身的可否写状态
        if (key != null && key.isValid() && key.isWritable()) {
            // todo write schedule
        }
    }

    private void read(Session session) {

    }

    private void addSessions() {
        for (Session session = addSessionQueue.poll(); session != null; session = addSessionQueue.poll()) {
            addNow(session);
        }
    }

    private void addNow(Session session) {
        try {
            initSession(session);
            System.out.println("new session from " + ((SocketChannel)session.getChannel()).socket());
        }
        catch (Exception e) {
            ExceptionSupervisor.getInstance().exceptionCaught(e);

            try {
                destroySession(session);
            }
            catch (Exception e1) {
                ExceptionSupervisor.getInstance().exceptionCaught(e1);
            }
        }
    }

    private void initSession(Session session) throws Exception {
        SelectableChannel channel = (SelectableChannel) session.getChannel();
        channel.configureBlocking(false);

        session.setSelectionKey(channel.register(selector, SelectionKey.OP_READ, session));
    }

    private void removeSessions() {
        for (Session session = addSessionQueue.poll(); session != null; session = addSessionQueue.poll()) {
            removeNow(session);
        }
    }

    private void removeNow(Session session) {
        // todo clear session write request

        try {
            destroySession(session);
        }
        catch (Exception e) {
            // todo fire session exception caught event
        }
    }

    private void destroySession(Session session) throws Exception {
        SelectableChannel channel = (SelectableChannel) session.getChannel();

        SelectionKey key = session.getSelectionKey();
        if (key != null) {
            key.cancel();
        }

        if (channel.isOpen()) {
            channel.close();
        }
    }
}
