package cn.zjnktion.middleware.ioframework.nio;

import cn.zjnktion.middleware.ioframework.Handler;
import cn.zjnktion.middleware.ioframework.Processor;
import cn.zjnktion.middleware.ioframework.Session;

import java.nio.channels.Channel;
import java.nio.channels.SelectionKey;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author zjnktion
 */
public class NioTcpSession implements Session {

    private static final AtomicInteger ID_GEN = new AtomicInteger(0);
    private final int id;

    private final Channel channel;
    private SelectionKey selectionKey;

    private final Handler handler;

    private Processor processor;

    public NioTcpSession(Channel channel, Handler handler) {
        this.channel = channel;
        this.handler = handler;

        id = ID_GEN.incrementAndGet();
    }

    public int getId() {
        return id;
    }

    public Channel getChannel() {
        return channel;
    }

    public SelectionKey getSelectionKey() {
        return this.selectionKey;
    }

    public void setSelectionKey(SelectionKey key) {
        this.selectionKey = key;
    }

    public Handler getHandler() {
        return handler;
    }

    public void setProcessor(Processor processor) {
        this.processor = processor;
    }
}
