import cn.zjnktion.middleware.zson.Zson;

import java.util.regex.Pattern;

/**
 * @author zjnktion
 */
public class TestMain {

    private static final String GET_REGEX = "get(\\w+)";
    private static final Pattern GET_PATTERN = Pattern.compile(GET_REGEX);
    private static final String REPLACE_POS = "$1";

    public static void main(String[] args) {
//        int p = 0x61c88647;
//        int q = p;
//
//        System.out.println(p & 7);
//
//        p+=q;
//        System.out.println((p) & 7);
//        p+=q;
//        System.out.println((p) & 7);
//        p+=q;
//        System.out.println((p) & 7);
//        p+=q;
//        System.out.println((p) & 7);
//        p+=q;
//        System.out.println((p) & 7);
//        p+=q;
//        System.out.println((p) & 7);
//        p+=q;
//        System.out.println((p) & 7);
//        p+=q;
//        System.out.println((p) & 7);

        /*String methodName = "getUserName";
        char[] chars = GET_PATTERN.matcher(methodName).replaceAll(REPLACE_POS).toCharArray();
        if (Character.isUpperCase(chars[0])) {
            chars[0] = Character.toLowerCase(chars[0]);
        }
        String fieldName = String.valueOf(chars);
        System.out.println(fieldName);*/

        long t = System.currentTimeMillis();
        InnerClass i = new InnerClass();
        i.setName(null);
        i.setPassword("123456");
        System.out.println(Zson.toJsonString(i));
        System.out.println(System.currentTimeMillis() - t);

        t = System.currentTimeMillis();
        InnerClass i2 = new InnerClass();
        i2.setName("zhengjn");
        i2.setPassword("123456");
        System.out.println(Zson.toJsonString(i2));
        System.out.println(System.currentTimeMillis() - t);
    }

    static class InnerClass {
        private String name;
        private String password;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}
