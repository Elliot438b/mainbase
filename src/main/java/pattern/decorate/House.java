package pattern.decorate;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class House {
    public final static Logger logger = LogManager.getLogger();

    public abstract void show();

}
