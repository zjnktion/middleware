package cn.zjnktion.middleware.zson.serializer;

import cn.zjnktion.middleware.zson.Context;
import cn.zjnktion.middleware.zson.writer.ZsonWriter;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.regex.Pattern;

/**
 * @author zjnktion
 */
public class ReferenceSerializer implements ZsonSerializer {

    public static final ReferenceSerializer INSTANCE = new ReferenceSerializer();

    private ReferenceSerializer() {

    }

    private static final String GET_REGEX = "get(\\w+)";
    private static final Pattern GET_PATTERN = Pattern.compile(GET_REGEX);
    private static final String REPLACE_POS = "$1";

    public void serialize(ZsonWriter writer, Object obj) throws IOException {
        if (obj == null) {
            writer.writeNull();
        }
        else {
            writer.write('{');

            Method[] methods = obj.getClass().getMethods();
            for (int i = 0, j = 0; i < methods.length; i++) {
                if (checkGetter(methods[i])) {
                    if (j > 0) {
                        writer.write(',');
                    }

                    String methodName = methods[i].getName();
                    char[] chars = GET_PATTERN.matcher(methodName).replaceAll(REPLACE_POS).toCharArray();
                    if (Character.isUpperCase(chars[0])) {
                        chars[0] = Character.toLowerCase(chars[0]);
                    }
                    String fieldName = String.valueOf(chars);
                    Object fieldValue = null;
                    try {
                        methods[i].setAccessible(true);
                        fieldValue = methods[i].invoke(obj);
                    }
                    catch (Exception e) {
                        fieldValue = null;
                    }

                    char[] fieldBuffer = new char[fieldName.length() + 2];
                    fieldBuffer[0] = '\"';
                    fieldName.getChars(0, fieldName.length(), fieldBuffer, 1);
                    fieldBuffer[fieldBuffer.length - 1] = '\"';
                    writer.write(fieldBuffer);

                    writer.write(':');

                    if (fieldValue == null) {
                        writer.writeNull();
                    }
                    else {
                        ZsonSerializer serializer = Context.getSerializer(fieldValue.getClass());
                        serializer.serialize(writer, fieldValue);
                    }

                    j++;
                }
            }

            writer.write('}');
        }
    }

    private boolean checkGetter(Method method) {
        return Pattern.matches(GET_REGEX, method.getName()) && method.getReturnType() != void.class && method.getReturnType() != Void.class && method.getReturnType() != Class.class && method.getParameterTypes().length == 0;
    }
}
