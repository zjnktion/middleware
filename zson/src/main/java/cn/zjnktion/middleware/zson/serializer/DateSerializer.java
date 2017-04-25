package cn.zjnktion.middleware.zson.serializer;

import cn.zjnktion.middleware.zson.writer.ZsonWriter;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author zjnktion
 */
public class DateSerializer implements ZsonSerializer {

    public static final DateSerializer INSTANCE = new DateSerializer();

    private DateSerializer() {

    }

    private static final DateFormat DF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public void serialize(ZsonWriter writer, Object obj) throws IOException {
        Date date = (Date) obj;
        if (date == null) {
            writer.writeNull();
        }
        else {
            writer.write(DF.format(date));
        }
    }
}
