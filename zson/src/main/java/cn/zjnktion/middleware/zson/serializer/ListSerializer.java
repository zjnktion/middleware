package cn.zjnktion.middleware.zson.serializer;

import cn.zjnktion.middleware.zson.Context;
import cn.zjnktion.middleware.zson.writer.ZsonWriter;

import java.io.IOException;
import java.util.List;

/**
 * @author zjnktion
 */
public class ListSerializer implements ZsonSerializer {

    public static final ListSerializer INSTANCE = new ListSerializer();

    private ListSerializer() {

    }

    public void serialize(ZsonWriter writer, Object obj) throws IOException {
        if (obj == null) {
            writer.writeNull();
        }
        else {
            writer.write('[');

            List list = (List) obj;
            if (list != null && !list.isEmpty()) {
                int i = 0;
                for (Object o : list) {
                    if (i > 0) {
                        writer.write(',');
                    }

                    if (o == null) {
                        writer.writeNull();
                    }
                    else {
                        ZsonSerializer serializer = Context.getSerializer(o.getClass());
                        serializer.serialize(writer, o);
                    }
                }
            }

            writer.write(']');
        }
    }
}
