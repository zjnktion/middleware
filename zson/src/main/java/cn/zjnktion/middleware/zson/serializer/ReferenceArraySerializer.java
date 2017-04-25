package cn.zjnktion.middleware.zson.serializer;

import cn.zjnktion.middleware.zson.Context;
import cn.zjnktion.middleware.zson.writer.ZsonWriter;

import java.io.IOException;
import java.lang.reflect.Array;

/**
 * @author zjnktion
 */
public class ReferenceArraySerializer implements ZsonSerializer {

    public static final ReferenceArraySerializer INSTANCE = new ReferenceArraySerializer();

    private ReferenceArraySerializer() {

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

                Object o = Array.get(obj, i);
                if (o == null) {
                    writer.writeNull();
                }
                else {
                    ZsonSerializer serializer = Context.getSerializer(o.getClass());
                    serializer.serialize(writer, o);
                }
            }

            writer.write(']');
        }
    }
}
