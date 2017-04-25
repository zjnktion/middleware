package cn.zjnktion.middleware.zson.serializer;

import cn.zjnktion.middleware.zson.writer.ZsonWriter;

import java.io.IOException;

/**
 * @author zjnktion
 */
public class LongSerializer implements ZsonSerializer {

    public static final LongSerializer INSTANCE = new LongSerializer();

    private LongSerializer() {

    }

    public void serialize(ZsonWriter writer, Object obj) throws IOException {
        final Number number = (Number) obj;
        if (number == null) {
            writer.writeNull();
        }
        else {
            writer.write(number.longValue());
        }
    }
}
