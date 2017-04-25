package cn.zjnktion.middleware.zson.serializer;

import cn.zjnktion.middleware.zson.writer.ZsonWriter;

import java.io.IOException;

/**
 * @author zjnktion
 */
public class CharacterSerializer implements ZsonSerializer {

    public static final CharacterSerializer INSTANCE = new CharacterSerializer();

    private CharacterSerializer() {

    }

    public void serialize(ZsonWriter writer, Object obj) throws IOException {
        final Character c = (Character) obj;
        if (c == null) {
            writer.writeNull();
        }
        else {
            char[] cbuf = new char[3];
            cbuf[0] = '\'';
            cbuf[1] = c;
            cbuf[2] = '\'';
            writer.write(cbuf);
        }
    }
}
