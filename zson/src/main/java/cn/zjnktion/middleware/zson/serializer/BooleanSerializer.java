package cn.zjnktion.middleware.zson.serializer;

import cn.zjnktion.middleware.zson.writer.ZsonWriter;

import java.io.IOException;

/**
 * @author zjnktion
 */
public class BooleanSerializer implements ZsonSerializer {

    public static final BooleanSerializer INSTANCE = new BooleanSerializer();

    private BooleanSerializer() {

    }

    public void serialize(ZsonWriter writer, Object obj) throws IOException {
        final Boolean bool = (Boolean) obj;
        if (bool == null) {
            writer.writeNull();
        }
        else {
            writer.write(bool);
        }
    }
}
