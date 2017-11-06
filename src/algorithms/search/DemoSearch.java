package algorithms.search;

/**
 * 查找算法接口的实现类：Demo查找
 * 
 * @notice 在实现一个泛型接口的时候，要指定参数的类型
 * @author Evsward
 *
 */
public class DemoSearch implements SFunction<String, String> {

    @Override
    public void put(String key, String val) {
        // TODO Auto-generated method stub

    }

    @Override
    public int size() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public Iterable<String> keys() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String get(String key) {
        
        return "demo";
    }}
