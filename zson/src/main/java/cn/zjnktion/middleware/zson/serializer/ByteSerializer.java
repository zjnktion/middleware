package cn.zjnktion.middleware.zson.serializer;

import cn.zjnktion.middleware.zson.writer.ZsonWriter;

import java.io.IOException;

/**
 * @author zjnktion
 */
public class ByteSerializer implements ZsonSerializer {

    public static final ByteSerializer INSTANCE = new ByteSerializer();

    private ByteSerializer() {

    }

    public void serialize(ZsonWriter writer, Object obj) throws IOException {
        final Byte b = (Byte) obj;
        if (b == null) {
            writer.writeNull();
        }
        else {
            writer.write(b);
        }
    }
}
