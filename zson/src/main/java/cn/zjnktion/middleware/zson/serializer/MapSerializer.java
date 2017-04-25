package cn.zjnktion.middleware.zson.serializer;

import cn.zjnktion.middleware.zson.Context;
import cn.zjnktion.middleware.zson.writer.ZsonWriter;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

/**
 * @author zjnktion
 */
public class MapSerializer implements ZsonSerializer {

    public static final MapSerializer INSTANCE = new MapSerializer();

    private MapSerializer() {

    }

    public void serialize(ZsonWriter writer, Object obj) throws IOException {
        if (obj == null) {
            writer.writeNull();
        }
        else {
            writer.write('{');

            Map map = (Map) obj;
            if (map != null && !map.isEmpty()) {
                Iterator<Map.Entry> iterator = map.entrySet().iterator();
                int i = 0;
                while (iterator.hasNext()) {
                    Map.Entry entry = iterator.next();

                    if (i > 0) {
                        writer.write(',');
                    }

                    Object key = entry.getKey();
                    if (key == null) {
                        writer.writeNull();
                    }
                    else {
                        ZsonSerializer serializer = Context.getSerializer(key.getClass());
                        serializer.serialize(writer, key);
                    }

                    writer.write(':');

                    Object value = entry.getValue();
                    if (value == null) {
                        writer.writeNull();
                    }
                    else {
                        ZsonSerializer serializer = Context.getSerializer(value.getClass());
                        serializer.serialize(writer, value);
                    }

                    i++;
                }
            }

            writer.write('}');
        }
    }
}
