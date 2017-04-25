package cn.zjnktion.middleware.zson.serializer;

import cn.zjnktion.middleware.zson.writer.ZsonWriter;

import java.io.IOException;
import java.lang.reflect.Array;

/**
 * @author zjnktion
 */
public class ShortArraySerializer implements ZsonSerializer {

    public static final ShortArraySerializer INSTANCE = new ShortArraySerializer();

    private ShortArraySerializer() {

    }

    public void serialize(ZsonWriter writer, Object obj) throws IOException {
        if (obj == null) {
            writer.writeNull();
        }
        else {
            writer.write('[');

            for (int i = 0; i < Array.getLength(obj); i++) {
                if (i > 0) {
                    writer.write(',');
                }

                writer.write(Array.getShort(obj, i));
            }

            writer.write(']');
        }
    }
}
