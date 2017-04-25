package cn.zjnktion.middleware.zson.serializer;

import cn.zjnktion.middleware.zson.writer.ZsonWriter;

import java.io.IOException;

/**
 * @author zjnktion
 */
public class StringSerializer implements ZsonSerializer {

    public static final StringSerializer INSTANCE = new StringSerializer();

    private StringSerializer() {

    }

    public void serialize(ZsonWriter writer, Object obj) throws IOException {
        final CharSequence cs = (CharSequence) obj;
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
}
