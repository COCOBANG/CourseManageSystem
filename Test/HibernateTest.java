import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Jaho on 2017/5/29.
 */
public class HibernateTest {

    @Test
    public void testMap(){
        Map<String,Integer> map = new HashMap<String,Integer>();
        map.put("1",1);
        map.put("1",2);
        System.out.print(map.get("1"));
    }


}
