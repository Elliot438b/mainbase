package pattern.proxy.search;

public class ProxySearcher extends Searcher {

    private Searcher searcher = new RealSearcher();

    private int count;

    public String doSearch(String username, int sid) {
        if (validateUser(username)) {
            count++;
            return "times: " + count + " " + searcher.doSearch(username, sid);
        }
        return "Identity discrepancy";
    }

    private boolean validateUser(String username) {
        if ("jhon".equals(username))
            return true;
        return false;
    }
}
