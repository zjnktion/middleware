package cn.zjnktion.middleware.ioframework;

import java.nio.channels.Channel;
import java.nio.channels.SelectionKey;

/**
 * @author zjnktion
 */
public interface Session {

    int getId();

    Channel getChannel();

    SelectionKey getSelectionKey();

    void setSelectionKey(SelectionKey key);

    Handler getHandler();

    void setProcessor(Processor processor);
}
