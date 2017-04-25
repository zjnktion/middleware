import java.util.HashMap;
import java.util.Map;

/**
 * @author zjnktion
 */
public class TypeTest {

    public static void main(String[] args) {
        Map<Class, Integer> map = new HashMap<>();
        map.put(Integer.class, 1);
        map.put(Integer.TYPE, 2);
        map.put(int.class, 3);
        map.put(int[].class, 4);
        map.put(Integer[].class, 5);
        int[] i = new int[3];
        Integer[] ii = new Integer[3];
        System.out.println(map.get(i.getClass()));
        System.out.println(map.get(ii.getClass()));

        System.out.println(ii.getClass().isArray());
    }
}
