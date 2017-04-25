package cn.zjnktion.middleware.zson;

import cn.zjnktion.middleware.zson.exception.RuntimeIOException;
import cn.zjnktion.middleware.zson.serializer.ZsonSerializer;
import cn.zjnktion.middleware.zson.writer.ZsonWriter;
import cn.zjnktion.middleware.zson.writer.ZsonWriterAdapter;

import java.io.IOException;

/**
 * @author zjnktion
 */
public final class Zson {

    public static String toJsonString(Object obj) {
        ZsonWriter writer = new ZsonWriterAdapter();
        ZsonSerializer serializer = Context.getSerializer(obj.getClass());
        try {
            serializer.serialize(writer, obj);
            return writer.toString();
        }
        catch (IOException e) {
            throw new RuntimeIOException("Zson occur an io exception while cast an object to json string.", e);
        }
    }
}
