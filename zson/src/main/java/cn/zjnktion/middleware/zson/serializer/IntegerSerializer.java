package cn.zjnktion.middleware.zson.serializer;

import cn.zjnktion.middleware.zson.writer.ZsonWriter;

import java.io.IOException;

/**
 * @author zjnktion
 */
public class IntegerSerializer implements ZsonSerializer {

    public static final IntegerSerializer INSTANCE = new IntegerSerializer();

    private IntegerSerializer() {

    }

    public void serialize(ZsonWriter writer, Object obj) throws IOException {
        final Number number = (Number) obj;
        if (number == null) {
            writer.writeNull();
        }
        else {
            writer.write(number.intValue());
        }
    }
}
