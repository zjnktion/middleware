package cn.zjnktion.middleware.zson.serializer;

import cn.zjnktion.middleware.zson.writer.ZsonWriter;

import java.io.IOException;

/**
 * @author zjnktion
 */
public class DoubleSerializer implements ZsonSerializer {

    public static final DoubleSerializer INSTANCE = new DoubleSerializer();

    private DoubleSerializer() {

    }

    public void serialize(ZsonWriter writer, Object obj) throws IOException {
        final Number number = (Number) obj;
        if (number == null) {
            writer.writeNull();
        }
        else {
            writer.write(number.doubleValue());
        }
    }
}
