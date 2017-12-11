package pattern.proxy.search;

import java.util.HashMap;
import java.util.Map;

public class RealSearcher extends Searcher {
    private Map<Integer, String> data = new HashMap<Integer, String>();

    RealSearcher() {// 模仿数据源，对象构造时初始化数据
        data.put(1001, "fridge");
        data.put(1002, "book");
        data.put(1003, "macrowave oven");
    }

    public String doSearch(String username, int sid) {
        return data.get(sid);
    }
}
