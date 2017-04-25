package cn.zjnktion.middleware.zson.serializer;

import cn.zjnktion.middleware.zson.writer.ZsonWriter;

import java.io.IOException;
import java.lang.reflect.Array;

/**
 * @author zjnktion
 */
public class StringArraySerializer implements ZsonSerializer {

    public static final StringArraySerializer INSTANCE = new StringArraySerializer();

    private StringArraySerializer() {

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

                CharSequence cs = (CharSequence) Array.get(obj, i);
                if (cs == null) {
                    writer.writeNull();
                }
                else {
                    char[] cbuf = new char[cs.length() + 2];
                    cbuf[0] = '\"';
                    cs.toString().getChars(0, cs.length(), cbuf, 1);
                    cbuf[cbuf.length - 1] = '\"';
                    writer.write(cbuf);
                }
            }

            writer.write(']');
        }
    }
}
